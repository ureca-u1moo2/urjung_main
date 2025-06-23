package com.eureka.ip.team1.urjung_main.forbiddenword.service;

import com.eureka.ip.team1.urjung_main.chatbot.entity.ForbiddenWord;
import com.eureka.ip.team1.urjung_main.forbiddenword.repository.ForbiddenWordRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ForbiddenWordServiceImpl implements ForbiddenWordService {

    private final ForbiddenWordRepository forbiddenWordRepository;

    public ForbiddenWordServiceImpl(ForbiddenWordRepository forbiddenWordRepository) {
        this.forbiddenWordRepository = forbiddenWordRepository;
    }

    // 노드 정의
    private static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        TrieNode fail = null;       // 실패 링크
        boolean isEnd = false;      // 금칙어 끝 표시
    }

    private final TrieNode root = new TrieNode();

    private final Set<String> specialForbiddenWords = new HashSet<>();  // 특수문자 금칙어 저장

    @PostConstruct
    public void init() {
        List<String> forbiddenWords = forbiddenWordRepository.findAll()
                .stream()
                .map(ForbiddenWord::getWord)
                .toList();

        System.out.println("=== 금칙어 리스트 ===");
        forbiddenWords.forEach(System.out::println);

        for (String w : forbiddenWords) insert(w);
        buildFailureLinks();
    }

    @Override
    public void reloadForbiddenWords() {
        synchronized (root) {
            root.children.clear();
            root.fail = root;
            root.isEnd = false;
            specialForbiddenWords.clear();

            List<String> forbiddenWords = forbiddenWordRepository.findAll()
                    .stream()
                    .map(ForbiddenWord::getWord)
                    .toList();

            for (String w : forbiddenWords) insert(w);
            buildFailureLinks();

            System.out.println("금칙어 재로딩 완료");
        }
    }

    private void insert(String word) {
        if (isSpecialWord(word)) {
            specialForbiddenWords.add(word);  // 특수문자 금칙어는 Set에 저장
            return;
        }

        String normalized = normalize(word);

        if (normalized.isEmpty()) return;  // 정규화 후 빈 문자열 무시

        TrieNode node = root;
        for (char c : normalized.toCharArray()) {
            node = node.children.computeIfAbsent(c, k -> new TrieNode());
        }
        node.isEnd = true;
    }

    private void buildFailureLinks() {
        Queue<TrieNode> q = new ArrayDeque<>();
        root.fail = root;

        for (TrieNode child : root.children.values()) {
            child.fail = root;
            q.add(child);
        }

        while (!q.isEmpty()) {
            TrieNode curr = q.poll();
            for (Map.Entry<Character, TrieNode> e : curr.children.entrySet()) {
                char c = e.getKey();
                TrieNode next = e.getValue();

                TrieNode f = curr.fail;
                while (f != root && !f.children.containsKey(c)) {
                    f = f.fail;
                }
                next.fail = f.children.getOrDefault(c, root);
                next.isEnd |= next.fail.isEnd;
                q.add(next);
            }
        }
    }

    @Override
    public boolean containsForbiddenWord(String text) {
        String normalized = normalize(text);
        TrieNode node = root;

        for (char c : normalized.toCharArray()) {
            while (node != root && !node.children.containsKey(c)) {
                node = node.fail;
            }
            node = node.children.getOrDefault(c, root);
            if (node.isEnd) return true;
        }

        // 특수문자 금칙어 원본 기준 검사
        for (String forbidden : specialForbiddenWords) {
            if (text.contains(forbidden)) return true;
        }

        return false;
    }

    private String normalize(String text) {
        return text.replaceAll("[^가-힣ㄱ-ㅎㅏ-ㅣa-zA-Z0-9]", "")
                .toLowerCase()
                .replaceAll("\\s+", "");
    }

    private boolean isSpecialWord(String word) {
        return word.replaceAll("[가-힣ㄱ-ㅎㅏ-ㅣa-zA-Z0-9]", "").length() > 0;
    }

    @Override
    public String censor(String text) {
        return containsForbiddenWord(text)
                ? "입력할 수 없는 단어가 포함되어 있습니다."
                : "호출되었습니다.";
    }
}

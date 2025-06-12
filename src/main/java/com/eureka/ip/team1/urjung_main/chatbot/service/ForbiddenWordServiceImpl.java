package com.eureka.ip.team1.urjung_main.chatbot.service;

import com.eureka.ip.team1.urjung_main.chatbot.entity.ForbiddenWord;
import com.eureka.ip.team1.urjung_main.chatbot.repository.ForbiddenWordRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.sql.SQLOutput;
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

    // 초기화
    @PostConstruct
    public void init() {
        List<String> forbiddenWords = forbiddenWordRepository.findAll()// 테스트용 디비 금칙어 호출
                .stream()
                .map(ForbiddenWord::getWord)
                .toList();

        System.out.println("=== 금칙어 리스트 ===");
        forbiddenWords.forEach(System.out::println);

//      List<String> forbiddenWords = Arrays.asList("바보", "멍청이", "시발", "병신", "ㅂㅅ", "ㅄ", "개새끼");

        // 1) Trie에 삽입
        for (String w : forbiddenWords) insert(w);

        // 2) 실패 링크 계산
        buildFailureLinks();
    }

    //관리자 페이지에서 금칙어를 관리하기 위한 부분 관리자 부분 붙으면 주석 풀어서 진행하겠습니다.
    @Override
    public void reloadForbiddenWords() {
        synchronized (root) {
            root.children.clear();
            root.fail = root;
            root.isEnd = false;

            List<String> forbiddenWords = forbiddenWordRepository.findAll()
                    .stream()
                    .map(ForbiddenWord::getWord)
                    .toList();

            for (String w : forbiddenWords) insert(w);
            buildFailureLinks();

            System.out.println("금칙어 재로딩 완료");
        }
    }

    // Trie 삽입
    private void insert(String word) {

        TrieNode node = root;
        for (char c : word.toCharArray()) {
            node = node.children.computeIfAbsent(c, k -> new TrieNode());
        }
        node.isEnd = true;
    }

    // 실패 링크 계산
    private void buildFailureLinks() {
        Queue<TrieNode> q = new ArrayDeque<>();
        // 루트의 자식들은 실패 링크를 루트로
        root.fail = root;
        for (TrieNode child : root.children.values()) {
            child.fail = root;
            q.add(child);
        }
        // BFS로 다음 레벨 처리
        while (!q.isEmpty()) {
            TrieNode curr = q.poll();
            for (Map.Entry<Character, TrieNode> e : curr.children.entrySet()) {
                char c = e.getKey();
                TrieNode next = e.getValue();

                // 실패 링크를 curr.fail에서 c를 따라 내려가며 찾기
                TrieNode f = curr.fail;
                while (f != root && !f.children.containsKey(c)) {
                    f = f.fail;
                }
                if (f.children.containsKey(c)) {
                    next.fail = f.children.get(c);
                } else {
                    next.fail = root;
                }
                // 실패 링크가 금칙어 끝이면 전파
                next.isEnd |= next.fail.isEnd;

                q.add(next);
            }
        }
    }

    // 한 번에 스캔하며 금칙어 검사 // facade 에서 사용할 수 있도록 private -> public 으로 변경
    @Override
    public boolean containsForbiddenWord(String text) {
        String normalized = normalize(text);
        TrieNode node = root;
        for (char c : normalized.toCharArray()) {   // text -> normalized 로 변경
            // 자식이 없으면 실패 링크 따라 올라가기
            while (node != root && !node.children.containsKey(c)) {
                node = node.fail;
            }
            // 있으면 내려가기, 없으면 루트 유지
            node = node.children.getOrDefault(c, root);
            if (node.isEnd) return true;  // 금칙어 끝 노드 도달
        }
        return false;
    }

    /**
     * 입력 문자열을 정규화:
     * - 한글, 영문, 숫자만 남기고 나머지 특수문자는 제거
     * - 공백, 언더바, 기호 등을 제거해서 우회 방지
     */
    private String normalize(String text) {
        return text.replaceAll("[^가-힣a-zA-Z0-9]", "").toLowerCase();
    }


    @Override
    public String censor(String text) {
        String normalized = normalize(text);
        return containsForbiddenWord(normalized)
                ? "입력할 수 없는 단어가 포함되어 있습니다."
                : "호출되었습니다.";
    }
}

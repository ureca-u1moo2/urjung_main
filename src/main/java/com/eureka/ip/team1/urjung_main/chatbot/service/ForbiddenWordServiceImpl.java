package com.eureka.ip.team1.urjung_main.chatbot.service;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ForbiddenWordServiceImpl implements ForbiddenWordService {

    /**
     * Trie 노드 정의 : 각 문자를 자식 노드로 가진다
     * fail : Aho-Corasick 실패 링크
     * isEnd : 금칙어 끝 표시
     */
    private static class  TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();    // 자식 문자 노드들
        TrieNode fail = null;
        boolean isEnd = false;  // 하나의 금칙어의 끝 문자인지 표시
    }

    private final TrieNode root = new TrieNode();   // trie 의 루트 노드 (비어있는 상태에서 시작)

    /**
     * 서비스가 시작될 때 자동으로 실행되는 초기화 메서드
     */

    @PostConstruct
    public void init() {
        // 우선은 하드코딩으로 금칙어 목록을 짰습니다.(추후 DB 연동 가능)
        List<String> forbiddenWords = Arrays.asList("바보", "멍청이", "시발", "병신",
                "ㅂㅅ", "ㅄ", "개새끼" );
        // Trie 에 삽입
        for (String word : forbiddenWords) {
             insert(word);
        }
        // 실패 링크 계산 (Aho-Corasick)
        buildFailureLinks();
    }

    /**
     * trie 에 단어를 삽입하는 메서드
     */

    private void insert(String word) {
        TrieNode node = root;   // root 노드부터 시작
        for (char c : word.toCharArray()) {
            // 문자를 하나씩 따라가며 노드 생성 또는 이동
            node = node.children.computeIfAbsent(c, k -> new TrieNode());
        }
        node.isEnd = true;  // 단어의 마지막 글자에 도달했으면 금칙어 끝 표시
    }
    /**
     * 모든 노드에 대해 실패 링크 계산
     **/

    private void buildFailureLinks() {
        Queue<TrieNode> queue = new ArrayDeque<>();
        // 루트의 자식노드 실패 링크는 루트로 설정
        root.fail = root;
        for (TrieNode child : root.children.values()) {
            child.fail = root;
            queue.add(child);
        }
        // BFS로 트리 순회
        while (!queue.isEmpty()) {
            TrieNode node = queue.poll();
            for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
                char c = entry.getKey();
                TrieNode next = entry.getValue();
                // 실패 링크를 따라가며 같은 문자가 있는지 탐색
                TrieNode f = node.fail;
                while (f != root && ! f.children.containsKey(c)) {
                    f = f.fail;
                }
                if (f.children.containsKey(c)) {
                    next.fail = f.children.get(c);
                }else {
                    next.fail = root;
                }
                // 실패 링크가 금칙어 끝이면 전파
                next.isEnd |= next.fail.isEnd;
                queue.add(next);
            }
        }
    }

    // 텍스트 내 금칙어 검색 : 한번의 순회로 모든 패턴 검사

    private boolean containsForbiddenWord(String text) {
        TrieNode node = root;
        for (char c : text.toCharArray()) {
            // 자식이 없으면 실패 링크를 따라
            while (node != root && ! node.children.containsKey(c)) {
                node = node.fail;
            }
            // 있으면 해당 자식으로 이동, 없으면 루트 유지
            node = node.children.getOrDefault(c, root);
            if (node.isEnd) {
                return true;    // 금칙어 끝 노드 발견
            }
        }
        return false;
    }

    // 금칙어 포함 시 경고 메세지
    @Override
    public String censor (String text) {
        return containsForbiddenWord(text)
                ? "입력할 수 없는 단어가 포함되어 있습니다."
                : " 호출 되었습니다.";
    }
}

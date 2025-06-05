package com.eureka.ip.team1.urjung_main.chatbot.service;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ForbiddenWordServiceImpl implements ForbiddenWordService {

    // trie 의 한 노드를 표현하는 내부 클래스 라고 합니다.
    private static class  TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();    // 자식 문자 노드들
        boolean isEnd = false;  // 하나의 금칙어의 끝 문자인지 표시
    }

    private final TrieNode root = new TrieNode();   // trie 의 루트 노드 (비어있는 상태에서 시작)

    /**
     * 서비스가 시작될 때 자동으로 실행되는 초기화 메서드
     * 여기서 금칙어들을 trie 에 등록
     */

    @PostConstruct
    public void init() {
        // 우선은 하드코딩으로 금칙어 목록을 짰습니다.
        List<String> forbiddenWords = Arrays.asList("바보", "멍청이", "시발", "병신",
                "ㅂㅅ", "ㅄ", "개새끼" );
        // 금칙어 리스트를 하나씩 trie 에 삽입
        forbiddenWords.forEach(this::insert);
    }

    /**
     * trie 에 단어를 삽입하는 메서드
     * 예 : "바보" -> "바" 노드 만들고 -> "보" 노드 만들고 -> 마지막 글자에 isEnd = true
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
     문장 안에 금칙어가 포함되어있는지 검사하는 메서드
     문장의 각 위치(i) 부터 시작해서 trie 에 따라가며 금칙어와 일치하는 부분이 있는지 확인
     한번이라도 발견되면 true 반환
     */

    private boolean containsForbiddenWord(String text) {
        for (int i = 0; i < text.length(); i++) {
            TrieNode node = root;
            int j = i;

            // 문장의 i번째부터 j번째까지 문자로 trie 를 탐색
            while (j < text.length() && node.children.containsKey(text.charAt(j))) {
                node = node.children.get(text.charAt(j));
                // 금칙어의 끝에 도달한 경우 (일치하는 금칙어 발견)
                if (node.isEnd) return true;
                j++;    // 다음 문자로 이동
            }
        }
        return false;   // 문장 전체를 돌았는데 금칙어가 없으면 false
    }

    @Override
    public String censor (String text) {
        return containsForbiddenWord(text)
                ? "입력할 수 없는 단어가 포함되어 있습니다."
                : " 호출 되었습니다.";
    }
}

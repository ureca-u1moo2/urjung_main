package com.eureka.ip.team1.urjung_main.embedding.config;

public class PlanEmbeddingFormatter {

    public static String formatForEmbedding(String name, int price, long dataAmount, long callAmount, long smsAmount, String originalDescription) {
        StringBuilder sb = new StringBuilder();

        // 1. 기본 정보 문장 - 숫자 정보를 자연어로 설명
        sb.append(String.format("'%s' 요금제는 월 %,d원으로, ", name, price));

        sb.append(String.format("월 데이터는 약 %dMB이며, ", dataAmount));
        sb.append(String.format("음성 통화는 %,d분, 문자는 %,d건이 제공됩니다. ", callAmount, smsAmount));

        // 2. 가격대 의미 해석
        if (price <= 15000) {
            sb.append("매우 저렴한 가격대로, 경제적인 통신비를 원하는 분에게 적합합니다. ");
        } else if (price <= 40000) {
            sb.append("중간 가격대의 요금제로, 기본적인 통신 서비스를 무난하게 이용할 수 있습니다. ");
        } else {
            sb.append("프리미엄 요금제로, 다양한 서비스를 부담 없이 사용할 수 있는 여유 있는 고객에게 적합합니다. ");
        }

        // 3. 데이터 사용량 기반 서술
        if (dataAmount == 0) {
            sb.append("데이터는 제공되지 않아, 주로 전화나 문자만 사용하는 분께 어울립니다. ");
        } else if (dataAmount <= 300) {
            sb.append("데이터 사용량이 매우 적은 고객을 위한 요금제로, 가벼운 웹서핑이나 메시징 정도만 사용할 수 있습니다. ");
        } else if (dataAmount <= 2048) {
            sb.append("간단한 웹 검색, 메신저, SNS 위주 사용에 적합한 요금제입니다. ");
        } else if (dataAmount <= 10240) {
            sb.append("일반적인 인터넷 사용이나 영상 시청도 가능한, 중간 수준의 데이터 요금제입니다. ");
        } else {
            sb.append("유튜브, 넷플릭스 같은 영상 스트리밍이나 대용량 데이터를 자주 사용하는 고객에게 적합합니다. ");
        }

        // 4. 통화량 기반 서술
        if (callAmount == 0) {
            sb.append("음성 통화는 제공되지 않으며, 데이터를 중심으로 사용하는 고객에게 맞습니다. ");
        } else if (callAmount <= 100) {
            sb.append("통화 시간이 적어, 전화 사용이 많지 않은 분에게 유리합니다. ");
        } else if (callAmount >= 99999) {
            sb.append("통화가 무제한으로 제공되어, 전화 통화를 자주 하는 분께 매우 유리합니다. ");
        }

        // 5. 타겟 사용자 키워드 포함 (문맥으로 자연스럽게)
        String lowerName = name.toLowerCase();
        String lowerDesc = originalDescription == null ? "" : originalDescription.toLowerCase();

        if (lowerName.contains("청소년") || lowerDesc.contains("청소년")) {
            sb.append("청소년을 위한 전용 요금제로, 미성년자나 학생들이 합리적인 요금으로 통신 서비스를 이용할 수 있도록 설계되었습니다. ");
        }

        if (lowerName.contains("시니어") || lowerDesc.contains("어르신") || lowerDesc.contains("노인")) {
            sb.append("어르신을 위한 시니어 요금제로, 간단한 통화와 안전 기능을 중심으로 구성되었습니다. ");
        }

        if (lowerName.contains("키즈") || lowerName.contains("어린이") || lowerDesc.contains("어린이")) {
            sb.append("자녀를 위한 키즈폰 사용자에게 적합한 요금제입니다. ");
        }

        if (lowerName.contains("스마트워치") || lowerDesc.contains("스마트워치")) {
            sb.append("스마트워치에 특화된 요금제로, 데이터와 통화 사용량이 적은 웨어러블 기기에 적합합니다. ");
        }

        if (lowerName.contains("해외") || lowerDesc.contains("해외") || lowerName.contains("로밍")) {
            sb.append("해외 여행이나 외국 체류 고객을 위한 요금제로, 로밍이나 국제 통화 등을 고려한 구성이 포함됩니다. ");
        }

        if (lowerName.contains("스트리밍") || lowerDesc.contains("유튜브") || lowerName.contains("OTT") || lowerDesc.contains("영상")) {
            sb.append("영상 콘텐츠 시청, 스트리밍 서비스를 자주 사용하는 고객에게 최적화된 요금제입니다. ");
        }

        if (lowerName.contains("나눠쓰기") || lowerDesc.contains("공유")) {
            sb.append("두 개 이상의 기기나 회선에서 데이터를 공유하고자 하는 고객에게 적합합니다. ");
        }

        if (lowerName.contains("안심") || lowerDesc.contains("위치")) {
            sb.append("보호자 위치 확인 기능 등 가족 안심 서비스를 중시하는 고객을 위한 요금제입니다. ");
        }

        // 6. 원래 설명 추가
        if (originalDescription != null && !originalDescription.isBlank()) {
            sb.append(originalDescription.trim());
            if (!originalDescription.trim().endsWith(".")) {
                sb.append(".");
            }
        }

        return sb.toString().trim();
    }
}

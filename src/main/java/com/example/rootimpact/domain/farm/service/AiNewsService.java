package com.example.rootimpact.domain.farm.service;

import com.example.rootimpact.domain.farm.dto.AiNewsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiNewsService {

    private final OpenAiService openAiService;

    public AiNewsResponse getCropNews(String cropName) {
        // ✅ 프롬프트 템플릿 생성
        String promptTemplate = """
                당신은 농업 기자입니다.
                다음 작물에 대한 최신 뉴스를 제공하세요: {cropName}.
                최근 연구, 병충해 예방, 재배 트렌드 등을 포함하여 정보를 요약해 주세요.
                """;

        // ✅ AI 호출
        String aiResponse = String.valueOf(openAiService.getRecommendation(
                //스트링은 보장은되지만? 왜쓰는지모르겟다
                promptTemplate,
                Map.of("cropName", cropName)
        ));

        return AiNewsResponse.builder()
                .news(aiResponse)
                .build();
    }
}
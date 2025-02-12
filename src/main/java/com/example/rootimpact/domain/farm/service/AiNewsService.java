package com.example.rootimpact.domain.farm.service;

import com.example.rootimpact.domain.farm.dto.AiNewsResponse;
import com.example.rootimpact.domain.farm.type.CropType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiNewsService {

    private final OpenAiService openAiService;

    public AiNewsResponse getCropNews(Long cropId) {
        // cropId로 CropInfo 조회하여 itemName 가져오기
        String cropName = CropType.getInfoById(cropId).getItemName();

        // ✅ 프롬프트 템플릿 생성
        String promptTemplate = """
                당신은 전국 각지 최고의 농업 기자입니다.
                                모든 제공은 당일 최근날짜 기준으로 생각합니다.
                                다음 작물에 대한 최신 뉴스를 제공하세요: {cropName}.
                                최근 연구, 병충해 예방, 재배 트렌드 등을 포함하여 정보를 요약해 주세요.
                                출하가격 작물 재배법도 한줄로 요약해주세요.
                                이 내용을 방법 3가지로 간략하게 말해줘.
                
                """;

        // ✅ AI 호출
        String aiResponse = String.valueOf(openAiService.getRecommendation(
                promptTemplate,
                Map.of("cropName", cropName)
        ));

        return AiNewsResponse.builder()
                .news(aiResponse)
                .build();
    }
}

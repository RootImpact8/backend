package com.example.rootimpact.domain.farm.service;

import com.example.rootimpact.domain.farm.dto.AiNewsResponse;
import com.example.rootimpact.domain.farm.dto.RdaVarietyResponse;
import com.example.rootimpact.domain.farm.dto.WeatherResponse;
import com.example.rootimpact.domain.farm.type.CropType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiNewsService {

    private final OpenAiService openAiService;
    private final WeatherService weatherService;
    private final RdaVarietyService rdaVarietyService;
    public AiNewsResponse getCropNews(Long userId, Long cropId) {
        // cropId로 CropInfo 조회하여 itemName 가져오기
        String cropName = CropType.getInfoById(cropId).getItemName();

        // ✅ 사용자 ID를 기반으로 날씨 정보 조회
        WeatherResponse weatherResponse = weatherService.getWeatherByUserId(userId);
        // ✅ 3. 사용자 관심 작물의 품종 정보 조회
        List<RdaVarietyResponse> varietyResponses = rdaVarietyService.getVarietyByUserCropId(userId, cropId);

        // ✅ 4. 가장 대표적인 품종 하나만 선택 (없으면 기본값 설정)
        RdaVarietyResponse varietyInfo = varietyResponses.isEmpty()
                ? new RdaVarietyResponse(cropName, "정보 없음", "해당 작물의 품종 정보가 없습니다.")
                : varietyResponses.get(0);


        // ✅ 5. 프롬프트 템플릿 생성
                String promptTemplate = """
[작물 최신 정보 제공]
오늘 날짜: {localtime}
작물: {cropName}
대표 품종: {varietyName}
주요 특성: {mainCharInfo}
위치: {location}

너는 최신 트렌드를 다 섭렵한 스마트 농업 기술 전문가, 정밀 농업기술자, 농업 기자로서 입력한 날짜 기준으로 최신 정보를 제공합니다.
다음 네 가지 항목에 대해 간단하고 이해하기 쉽게 설명해 주세요.

1. 최신 연구 및 뉴스:
   - 최신 연구 결과와 오늘의 농업 관련 뉴스를 간단히 정리해 주세요.

2. 출하 가격 및 시장 동향:
   - {cropName}의 평균 출하 가격과 최근 시장 동향(수요 변화 등)을 표 형식이나 간략한 문장으로 설명해 주세요.

3. 재배법 및 관리 팁:
   - 작물 재배에 필요한 주요 재배법과 관리 팁을 쉽게 안내해 주세요.

4. 대표 품종 정보:
   - 대표 품종의 주요 특성과 장점을 간략하게 요약해 주세요.

각 항목은 한 단락으로 구분하여, 친절하고 쉽게 설명해 주세요.
""";

        // ✅ 6. 프롬프트에 데이터 삽입
        String aiResponse = String.valueOf(openAiService.getRecommendation(
                promptTemplate,
                Map.of(
                        "cropName", cropName,
                        "localtime", weatherResponse.getLocation().getLocaltime(),
                        "location", weatherResponse.getLocation().getName(),
                        "weather", weatherResponse.getCurrent().getCondition().getText(),
                        "varietyName", varietyInfo.getVarietyName(),
                        "mainCharInfo", varietyInfo.getMainCharInfo()
                )
        ));

        return AiNewsResponse.builder()
                .news(aiResponse)
                .build();
    }
}


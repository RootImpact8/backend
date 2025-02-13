package com.example.rootimpact.domain.farm.service;

import com.example.rootimpact.domain.farm.dto.AiNewsResponse;
import com.example.rootimpact.domain.farm.dto.WeatherResponse;
import com.example.rootimpact.domain.farm.type.CropType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiNewsService {

    private final OpenAiService openAiService;
    private final WeatherService weatherService;
    public AiNewsResponse getCropNews(Long userId, Long cropId) {
        // cropId로 CropInfo 조회하여 itemName 가져오기
        String cropName = CropType.getInfoById(cropId).getItemName();

        // ✅ 사용자 ID를 기반으로 날씨 정보 조회
        WeatherResponse weatherResponse = weatherService.getWeatherByUserId(userId);

        // ✅ 프롬프트 템플릿 생성
        String promptTemplate = """
        오늘 날짜를 알려줘 {localtime}
        당신은 전국 최고의 농업 기자입니다.
        모든 제공은 사용자가 요청한 날짜 기준으로 최신 정보를 반영해야 합니다.
        사용자가 요청한 "{cropName}"에 대한 최신 농업 정보를 제공합니다.
        다음 내용을 포함하여 간략하고 이해하기 쉬운 3단락으로 정리하세요.
        
        1️⃣ **최신 연구 및 병충해 예방**
        - 최근 연구 및 새로운 품종 개발 소식 
        - 현재 {cropName}의 주요 병충해 정보 
        - 병충해 예방 및 해결 방법 
        
        2️⃣ **출하 가격 및 재배법 요약** 
        - 현재 {cropName}의 출하 가격(kg당 평균 가격) 
        - 최근 시장 동향(수요 증가/감소 등) 
        - {cropName}의 주요 재배법 요약 (심기 적합한 시기, 토양 조건, 온도) 
        
        3️⃣ **어르신들이 쉽게 이해할 수 있는 정보** 
        - 최신 연구 결과를 쉽게 설명 
        - 현재 날씨 기준으로 {cropName} 재배 적합성 평가 (위치: {location})
        - 출하량 변화 및 가격 정보 
        
        **추가 정보:** 
        - 오늘의 날짜 기준 최신 뉴스 반영 
        - {location} 지역의 날씨 정보: {weather}
        - 문장은 간결하고 이해하기 쉽게 작성 
        - 전문 용어는 쉬운 표현으로 변환 
    """;

        // ✅ 프롬프트에 데이터 삽입
        String aiResponse = String.valueOf(openAiService.getRecommendation(
                promptTemplate,
                Map.of(
                        "cropName", cropName,
                        "localtime",weatherResponse.getLocation().getLocaltime(),
                        "location", weatherResponse.getLocation().getName(),
                        "weather", weatherResponse.getCurrent().getCondition().getText()
                )
        ));

        return AiNewsResponse.builder()
                .news(aiResponse)
                .build();
    }
}

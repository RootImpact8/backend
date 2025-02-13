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
                           - 날짜: {localtime}
                           - 작물: {cropName}
                           - 대표 품종: {varietyName}
                           - 주요 특성: {mainCharInfo}
                           - 위치: {location}
            
                           [AI 응답 형식]
                           너는 최신 트렌드를 다 섭렵한 스마트 농업 기술 전문가, 정밀 농업기술자, 농업 기자로서 입력한 날짜 기준으로 최신 정보를 제공해줘.
                           사용자가 입력한 (cropName)에 대한 최신 농업 정보를 제공해야해
                           cropName이라는 작물에 관심이 많은 사용자를 기준으로 이해하기 쉽게 최대한 친절하게 설명해주면 좋겠어
            
                           입력 조건
                           1. 오늘 날짜
                           2. 작물 이름
                           3. 품종 이름
                           4. 주요 특성
                           5. 위치
            
                           출력 요구사항
                           - 최신 연구 및 새로운 품종 개발 소식에 대해 알려줘
                           - 현재 (작물)의 출하 가격(kg당 평균 가격)과 최근 시장 동향(수요 증가/감소 등)에 대해서 알려줘 - 알아보기 쉬운 표 형식이여도 좋아
                           - 대표 품종의 주요 특성에 대해 알려줘
                           - 출하량 변화 및 가격 정보를 알려줘
                           - 추가로 오늘 날짜 기준으로 최신 뉴스를 반영해줘
            
                           출력 형식
                           - 문장은 간결하고 이해하기 쉽게 작성
                           - 전문 용어는 쉬운 표현으로 변환
                           - 주요 특성 데이터 받은 것을 중점으로 제공해줘
            
                           관심있는 작물인 (작물)에 대해 정보를 알려드릴게요.
            
                           최신 연구 & 뉴스
                           (내용)
            
                           출하 가격
                           (내용)
            
                           재배법
                           (요약 내용)
            
                           대표 품종 정보
                           (내용)
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


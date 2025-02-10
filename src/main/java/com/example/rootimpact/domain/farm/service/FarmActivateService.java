package com.example.rootimpact.domain.farm.service;

import com.example.rootimpact.domain.diary.entity.FarmDiary;
import com.example.rootimpact.domain.diary.repository.FarmDiaryRepository;
import com.example.rootimpact.domain.farm.dto.AiRecommendationResponse;
import com.example.rootimpact.domain.farm.dto.WeatherResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FarmActivateService {
    private final FarmDiaryRepository farmDiaryRepository;
    private final WeatherService weatherService;
    private final OpenAiService openAiService;

    /**
     * ✅ AI 기반 활동 추천 + 이상기후 대응 (작물 재배 일차 포함)
     */
    public AiRecommendationResponse getAiRecommendation(Long userId, String cropName) {
        // 1️⃣ 작물의 모든 영농일기 데이터 조회
        List<FarmDiary> diaries = farmDiaryRepository.findByUserIdAndUserCrop_CropNameOrderByWriteDateAsc(userId, cropName);

        if (diaries.isEmpty()) {
            return AiRecommendationResponse.builder()
                    .cropStage("해당 작물의 재배 기록이 없습니다.")
                    .answer("현재 해당 작물에 대한 영농일기가 없습니다. 활동을 기록해 주세요!")
                    .build();
        }

        // 2️⃣ 현재 위치 기반 날씨 데이터 조회
        WeatherResponse weatherResponse = weatherService.getWeatherByUserId(userId);

        // 3️⃣ 작물 재배 시작일 계산 (가장 오래된 일기 기준)
        LocalDate firstDiaryDate = diaries.get(0).getWriteDate();
        long daysPassed = ChronoUnit.DAYS.between(firstDiaryDate, LocalDate.now());

        // 4️⃣ AI에게 전달할 영농일기 상세 데이터 정리
        StringBuilder diaryDetails = new StringBuilder();
        for (FarmDiary diary : diaries) {
            diaryDetails.append(String.format(
                    "- 날짜: %s, 작업: %s, 내용: %s\n",
                    diary.getWriteDate(),
                    diary.getTask().getCategory(),
                    diary.getContent()
            ));
        }

        // 5️⃣ AI 프롬프트 생성
        String promptTemplate = """
            당신은 전문 농업 컨설턴트입니다.
            아래는 사용자가 기록한 영농일기와 현재 지역의 날씨 정보입니다.

            [현재 날씨]
            - 지역: {location}
            - 날씨 상태: {currentWeather}
            - 기온: {temperature}°C
            - 습도: {humidity}%
            - 강수량: {totalprecip_mm}mm

            [재배 정보]
            - 작물: {cropName}
            - 현재 {daysPassed}일차 진행 중
            - 최근 작업 내용:
            {diaryDetails}

            [AI 응답 지침]
            1. 이상 기후(폭염, 폭설, 폭우 등)일 경우, 먼저 경고 메시지를 출력하고 적절한 대처 방법을 알려주세요.
            2. 이상 기후가 아니라면, 현재까지의 작업을 고려하여 오늘 해야 할 작업을 2~3줄의 문장으로 설명하세요.

            최종적으로 "answer" 항목에 한글로 자연스럽게 전달하세요.
        """;

        // 6️⃣ AI 요청 변수 설정
        Map<String, Object> variables = Map.of(
                "cropName", cropName,
                "daysPassed", daysPassed,
                "diaryDetails", diaryDetails.toString(),
                "location", weatherResponse.getLocation().getName(),
                "currentWeather", weatherResponse.getCurrent().getCondition().getText(),
                "temperature", weatherResponse.getCurrent().getTemp_c(),
                "humidity", weatherResponse.getCurrent().getHumidity(),
                "totalprecip_mm", weatherResponse.getCurrent().getTotalprecip_mm()
        );

        // 7️⃣ AI 요청 수행
        String aiResponse = openAiService.getRecommendation(promptTemplate, variables);

        // 8️⃣ DTO 형태로 응답 반환
        return AiRecommendationResponse.builder()
                .cropStage(String.format("%s 재배 %d일차", cropName, daysPassed))
                .answer(aiResponse)
                .build();
    }
}
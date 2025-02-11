package com.example.rootimpact.domain.farm.service;

import com.example.rootimpact.domain.diary.entity.FarmDiary;
import com.example.rootimpact.domain.diary.repository.FarmDiaryRepository;
import com.example.rootimpact.domain.farm.dto.AiRecommendationResponse;
import com.example.rootimpact.domain.farm.dto.WeatherResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FarmActivateService {
    private final FarmDiaryRepository farmDiaryRepository;
    private final WeatherService weatherService;
    private final OpenAiService openAiService;

    /**
     * ✅ AI 기반 재배 활동 추천 및 이상기후 대응 서비스
     */
    public AiRecommendationResponse getAiRecommendation(Long userId, String cropName) {
        // 1️⃣ 사용자의 영농일기 데이터 조회
        List<FarmDiary> diaries = farmDiaryRepository.findByUserIdAndUserCrop_CropNameOrderByWriteDateAsc(userId, cropName);

        // 2️⃣ 영농일기가 없는 경우 기본 응답 반환
        if (diaries.isEmpty()) {
            return AiRecommendationResponse.builder()
                    .cropStage("해당 작물의 재배 기록이 없습니다.")
                    .summary("현재 해당 작물에 대한 영농일기가 없습니다. 활동을 기록해 주세요!")
                    .detailedAdvice(null)
                    .isExtremeWeather(false)
                    .build();
        }

        // 3️⃣ 현재 사용자의 위치 기반 날씨 데이터 조회
        WeatherResponse weatherResponse = weatherService.getWeatherByUserId(userId);

        // 4️⃣ 이상기후 여부 판단 (온도, 강수량, 날씨 상태 기반)
        boolean isExtremeWeather = isExtremeWeatherCondition(weatherResponse);

        // 5️⃣ 작물 재배 시작일 계산 (가장 오래된 일기 기준)
        long daysPassed = ChronoUnit.DAYS.between(diaries.get(0).getWriteDate(), LocalDate.now());

        // 6️⃣ 영농일기 데이터를 문자열로 변환 (AI가 읽을 수 있도록)
        StringBuilder diaryDetails = new StringBuilder();
        for (FarmDiary diary : diaries) {
            diaryDetails.append(String.format(
                    "- 날짜: %s, 작업: %s, 내용: %s\n",
                    diary.getWriteDate(),
                    diary.getTask().getCategory(),
                    diary.getContent()
            ));
        }

        // 7️⃣ AI 프롬프트 구성
        String promptTemplate;
        Map<String, Object> variables = Map.of(
                "cropName", cropName,
                "daysPassed", daysPassed,
                "location", weatherResponse.getLocation().getName(),
                "currentWeather", weatherResponse.getCurrent().getCondition().getText(),
                "temperature", weatherResponse.getCurrent().getTemp_c(),
                "humidity", weatherResponse.getCurrent().getHumidity(),
                "totalprecip_mm", weatherResponse.getCurrent().getTotalprecip_mm(),
                "diaryDetails", diaryDetails.toString()
        );

        if (isExtremeWeather) {
            // ✅ 이상기후 발생 시 AI 프롬프트 (한 번에 전체 응답 생성)
            promptTemplate = """
                현재 이상기후가 발생했습니다.
                - 지역: {location}
                - 날씨 상태: {currentWeather}
                - 기온: {temperature}°C
                - 강수량: {totalprecip_mm}mm
                
                [AI 응답 형식]
                이상기후 요약: (한 줄)
                이상기후 대응 방법: (4~5줄 자세히 설명)
            """;
        } else {
            // ✅ 정상 기후 시 AI 프롬프트 (한 번에 전체 응답 생성)
            promptTemplate = """
                [현재 날씨]
                - 지역: {location}
                - 날씨 상태: {currentWeather}
                - 기온: {temperature}°C
                - 습도: {humidity}%
                - 강수량: {totalprecip_mm}mm

                [재배 정보]
                - 작물: {cropName}
                - 현재 {daysPassed}일차 진행 중

                [과거 영농일기]
                {diaryDetails}

                [AI 응답 형식]
                요약: (한 줄)
                상세 설명: (4~5줄 자세히 설명)
            """;
        }

        // 8️⃣ AI 호출 및 응답 받아오기
        String aiResponse = openAiService.getRecommendation(promptTemplate, variables);

        // ✅ AI 원본 응답 확인 (디버깅용)
        log.info("🟢 AI 원본 응답: {}", aiResponse);

        // 9️⃣ 최종 응답 DTO 반환
        return isExtremeWeather
                ? AiRecommendationResponse.builder()
                .cropStage(String.format("%s 재배 %d일차", cropName, daysPassed))
                .isExtremeWeather(true)
                .climateWarning(aiResponse.split("\n")[0])  // 첫 줄이 요약
                .climateAdvice(aiResponse.substring(aiResponse.indexOf("\n") + 1))  // 나머지가 대처방안
                .build()
                : AiRecommendationResponse.builder()
                .cropStage(String.format("%s 재배 %d일차", cropName, daysPassed))
                .isExtremeWeather(false)
                .summary(aiResponse.split("\n")[0])  // 첫 줄이 요약
                .detailedAdvice(aiResponse.substring(aiResponse.indexOf("\n") + 1))  // 나머지가 상세 설명
                .build();
    }

    /**
     * ✅ 이상기후 판별 로직
     */
    private boolean isExtremeWeatherCondition(WeatherResponse weatherResponse) {
        double temp = weatherResponse.getCurrent().getTemp_c();
        double rain = weatherResponse.getCurrent().getTotalprecip_mm();
        String condition = weatherResponse.getCurrent().getCondition().getText();

        return temp < -5 || temp > 35 || rain > 50 || condition.contains("폭우") || condition.contains("태풍") || condition.contains("한파");
    }
}
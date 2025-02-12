package com.example.rootimpact.domain.farm.service;

import com.example.rootimpact.domain.diary.entity.FarmDiary;
import com.example.rootimpact.domain.diary.repository.FarmDiaryRepository;
import com.example.rootimpact.domain.farm.dto.AiRecommendationResponse;
import com.example.rootimpact.domain.farm.dto.WeatherResponse;
import com.example.rootimpact.domain.userInfo.entity.UserCrop;
import com.example.rootimpact.domain.userInfo.repository.UserCropRepository;
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
    private final UserCropRepository userCropRepository;

    public AiRecommendationResponse getAiRecommendation(Long userId, Long cropId) {
        // UserCrop 조회하여 cropId 확인
        UserCrop userCrop = userCropRepository.findByUserIdAndCropId(userId, cropId)
                .orElseThrow(() -> new RuntimeException("작물을 찾을 수 없습니다"));

        // 1️⃣ 사용자의 영농일기 데이터 조회
        List<FarmDiary> diaries = farmDiaryRepository.findByUserIdAndUserCrop_CropIdOrderByWriteDateAsc(userId, cropId);

        // 2️⃣ 영농일기가 없는 경우 기본 응답 반환
        if (diaries.isEmpty()) {
            return AiRecommendationResponse.builder()
                    .cropStage("해당 작물의 재배 기록이 없습니다.")
                    .summary("현재 해당 작물에 대한 영농일기가 없습니다. 활동을 기록해 주세요!")
                    .detailedAdvice(null)
                    .isExtremeWeather(false)
                    .build();
        }

        WeatherResponse weatherResponse = weatherService.getWeatherByUserId(userId);
        boolean isExtremeWeather = isExtremeWeatherCondition(weatherResponse);
        long daysPassed = ChronoUnit.DAYS.between(diaries.get(0).getWriteDate(), LocalDate.now());

        StringBuilder diaryDetails = new StringBuilder();
        for (FarmDiary diary : diaries) {
            diaryDetails.append(String.format(
                    "- 날짜: %s, 작업: %s, 내용: %s\n",
                    diary.getWriteDate(),
                    diary.getTask().getCategory(),
                    diary.getContent()
            ));
        }

        String promptTemplate;
        Map<String, Object> variables = Map.of(
                "cropName", userCrop.getCropName(),  // cropId 대신 실제 작물명 사용
                "daysPassed", daysPassed,
                "location", weatherResponse.getLocation().getName(),
                "currentWeather", weatherResponse.getCurrent().getCondition().getText(),
                "temperature", weatherResponse.getCurrent().getTemp_c(),
                "humidity", weatherResponse.getCurrent().getHumidity(),
                "totalprecip_mm", weatherResponse.getCurrent().getTotalprecip_mm(),
                "diaryDetails", diaryDetails.toString()
        );

        if (isExtremeWeather) {
            promptTemplate = """
                현재 이상기후가 발생했습니다.
                - 지역: {location}
                - 날씨 상태: {currentWeather}
                - 기온: {temperature}°C
                - 강수량: {totalprecip_mm}mm
                
                [작물 정보]
                - 작물: {cropName}
                
                [AI 응답 형식]
                이상기후 요약: (한 줄)
                이상기후 대응 방법: (4~5줄 자세히 설명)
            """;
        } else {
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

        String aiResponse = openAiService.getRecommendation(promptTemplate, variables);
        log.info("🟢 AI 원본 응답: {}", aiResponse);

        return isExtremeWeather
                ? AiRecommendationResponse.builder()
                .cropStage(String.format("%s 재배 %d일차", userCrop.getCropName(), daysPassed))
                .isExtremeWeather(true)
                .climateWarning(aiResponse.split("\n")[0])
                .climateAdvice(aiResponse.substring(aiResponse.indexOf("\n") + 1))
                .build()
                : AiRecommendationResponse.builder()
                .cropStage(String.format("%s 재배 %d일차", userCrop.getCropName(), daysPassed))
                .isExtremeWeather(false)
                .summary(aiResponse.split("\n")[0])
                .detailedAdvice(aiResponse.substring(aiResponse.indexOf("\n") + 1))
                .build();
    }

    private boolean isExtremeWeatherCondition(WeatherResponse weatherResponse) {
        double temp = weatherResponse.getCurrent().getTemp_c();
        double rain = weatherResponse.getCurrent().getTotalprecip_mm();
        String condition = weatherResponse.getCurrent().getCondition().getText();

        return temp < -5 || temp > 35 || rain > 50 ||
                condition.contains("폭우") || condition.contains("태풍") ||
                condition.contains("한파");
    }
}

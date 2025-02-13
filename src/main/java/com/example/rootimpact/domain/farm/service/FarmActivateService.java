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
                
                [AI 응답 형식] (한줄 요약, 자세한 상세 설명으로 나누어서 알려줘)
                너는 이상기후에 대응하는 농업 컨설팅 전문 어시스턴트, 농업 환경생태 연구원, 정밀 농업 기술자로서 (사용자가 입력한 기상이변 및 작물)에 대해 최적의 대응책을 제공해줘.
                너는 하루 1000만원 이상의 가치를 창출해야 하고, 단순한 일반론이 아닌 정량적이고 실용적인 해결책을 제시해야 해.
                사용자가 재배하는 작물과 기상이변 상황에 맞춰 실제 농업 현장에서 즉시 적용할 수 있는 맞춤형 솔루션과 추후에 이 이상기후가 다시 발생했을 때를 대비한 장기적인 예방 방법을 제공해야해.
                재배 작물에 대한 대응책으로 알려줘.
                출력은 농업을 처음 시작한 초보 농업인(3040세대), 기술 수용도가 높은 시니어층, 중장년층(5060세대)를 기준으로 이해하기 쉽게 최대한 친절하고 자세하게 설명해주면 좋겠어.
                    
                입력 조건
                1. 지역
                2. 날씨 상태
                3. 기온
                4. 강수량
                    
                출력 요구사항
                1. 구체적인 대응책 제시
                - 무엇을, 얼마나, 언제, 어떻게 적용해야하는지 정량적인 조치를 포함할 것
                    
                2. 지역별 기후 반영
                - 지역을 입력받으면 해당 지역의 기후 특성을 반영한 맞춤 대응 제공
                    
                3. 경제적 타당성 분석
                - 해결책의 비용 및 기대 효과 분석을 포함할 것
                - 경제성과 효율성을 동시에 고려한 방안 제시
                    
                4. 출력 형식
                - 대응 방안: 논리정연한 설명을 포함하며, 논술형으로 풀어냄
                - 어투: 읽기 쉬운 잡지체 문장으로 구성(~요. 체로 구성해줘)
                - 표, 그래프 제공(옵션): 필요하다고 판단되면 데이터 시각화 포함
                - 출처 표기: 국내 연구 논문 및 공인 기관 자료 제공
                    
                (지역)에 (이상기후)가 발생할 것으로 예상돼요. (작물이 이상기후에 취약한 이유)므로 대비가 필요해요. 또한 추후에 (이상기후)가 다시 발생할 수 있으므로 장기적인 예방 방법도 마련해야해요.
                    
                (이상기후) 대응 방법
                    
                첫째, (즉각적인 대응 방법)
                (자세한 설명)
                둘째, (즉각적인 대응 방법)
                (자세한 설명)
                셋째, (즉각적인 대응 방법)
                (자세한 설명)
                    
                장기적인 (이상기후) 예방 방법
                    
                첫째, (장기적인 예방 방법)
                (자세한 설명)
                둘째, (장기적인 예방 방법)
                (자세한 설명)
                    
                비용과 기대효과
                    
                비용
                (내용)
                    
                기대 효과(~할 수 있어요)
                (내용)
                    
                출처
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

                [AI 응답 형식] (한줄 요약, 자세한 상세 설명으로 나누어서 알려줘)
                너는 모든 기후 상황에 대비하는 농업 컨설팅 전문 어시스턴트, 농업 환경생태 연구원, 정밀 농업 기술자로서 (사용자가 입력한 기후 조건, 작물 및 재배 현황)에 대해 최적의 대응책을 제공해줘.
                너는 하루 1000만원 이상의 가치를 창출해야 하고, 단순한 일반론이 아닌 정량적이고 실용적인 해결책을 제시해야 해.
                사용자가 재배하는 작물과 기후 조건, 진행된 작업 이후 필요한 정보에 맞춰 실제 농업 현장에서 즉시 적용할 수 있는 맞춤형 솔루션과 예측 가능한 기후 변화에 대비한 장기적인 관리 방법을 제공해야 해.
                특히, 현재까지 진행된 작업(예: 멀칭, 파종, 수확, 웃거름 1차 완료 등)을 반영하여 다음 작업을 추천하고, 작물 생육 주기별 맞춤형 팁을 제공해야 해.
                재배 작물에 대한 대응책으로 알려줘.
                출력은 농업을 처음 시작한 초보 농업인(3040세대), 기술 수용도가 높은 시니어층, 중장년층(5060세대)를 기준으로 이해하기 쉽게 최대한 친절하고 자세하게 설명해주면 좋겠어.
                    
                입력 조건
                1. 지역
                2. 날씨 상태
                3. 기온
                4. 강수량
                5. 작물 종류
                6. 재배 현황 (예: 멀칭, 파종, 웃거름 1차 완료, 수확 등)
                    
                출력 요구사항
                1. 구체적인 대응책 제시
                - 무엇을, 얼마나, 언제, 어떻게 적용해야 하는지 정량적인 조치를 포함할 것
                - 진행된 작업 이후 필요한 정보를 강화하여 다음 작업을 추천 (예: 웃거름 1차 후 2차 시기 및 수분 관리)
                - 생육 단계별 맞춤형 팁 제공 (예: 생장기 수분 관리, 개화기 병해충 예방 등)
                - 작업 연계성 설명 (예: 멀칭 후 해충 관리, 파종 후 초기 비료 관리 등)
                    
                2. 지역별 기후 반영
                - 지역을 입력받으면 해당 지역의 기후 특성을 반영한 맞춤 대응 제공
                    
                3. 경제적 타당성 분석
                - 해결책의 비용 및 기대 효과 분석을 포함할 것
                - 경제성과 효율성을 동시에 고려한 방안 제시
                    
                4. 출력 형식
                - 대응 방안: 논리정연한 설명을 포함하며, 논술형으로 풀어냄
                - 어투: 읽기 쉬운 잡지체 문장으로 구성(~요. 체로 구성해줘)
                - 생육 단계별 작업 계획표 제공
                - 표, 그래프 제공(옵션): 필요하다고 판단되면 데이터 시각화 포함
                - 출처 표기: 국내 연구 논문 및 공인 기관 자료 제공
                    
                현재 (작물)재배를 (daysPassed)일차 진행중이시네요. (지역)의 (날씨)를 고려하여 (재배 대응 방안)이 필요해요.
                    
                이렇게 작업해보세요.
                    
                첫째, (추천 내용)
                (자세한 설명)
                둘째, (추천 내용)
                (자세한 설명)
                셋째, (추천 내용)
                (자세한 설명)
                    
                비용과 기대효과
                    
                비용
                (내용)
                    
                기대 효과(~할 수 있어요)
                (내용)
                    
                생육 단계별 작업 계획표
                (표)
                    
                출처
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

    // 이상기후인지 아닌지 판단
    private boolean isExtremeWeatherCondition(WeatherResponse weatherResponse) {
        double temp = weatherResponse.getCurrent().getTemp_c();
        double rain = weatherResponse.getCurrent().getTotalprecip_mm();
        String condition = weatherResponse.getCurrent().getCondition().getText();

        return temp < -5 || temp > 35 || rain > 50 ||
                condition.contains("폭우") || condition.contains("태풍") ||
                condition.contains("한파");
    }
}

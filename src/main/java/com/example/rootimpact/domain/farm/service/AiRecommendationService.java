package com.example.rootimpact.domain.farm.service;

import com.example.rootimpact.domain.farm.dto.AiRecommendationResponse;
import com.example.rootimpact.domain.farm.dto.WeatherResponse;
import com.example.rootimpact.domain.user.entity.User;
import com.example.rootimpact.domain.userInfo.entity.UserCrop;
import com.example.rootimpact.domain.userInfo.service.UserInfoService;
import com.example.rootimpact.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiRecommendationService {

    private final OpenAiService openAiService;
    private final WeatherService weatherService;
    private final UserInfoService userInfoService;
    private final UserRepository userRepository;

    public AiRecommendationResponse getRecommendation(Authentication authentication) {
        // ✅ 1. 사용자 이메일 및 ID 가져오기
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ 2. 사용자의 첫 번째 재배 작물 가져오기
        List<UserCrop> cultivatedCrops = userInfoService.getCultivatedCrops(user.getId());
        if (cultivatedCrops.isEmpty()) {
            throw new RuntimeException("No cultivated crops found for the user.");
        }
        String cropName = cultivatedCrops.get(0).getCropName(); // 첫 번째 작물

        // ✅ 3. 사용자 거주 지역 기반 날씨 데이터 가져오기
        WeatherResponse weather = weatherService.getWeather(authentication);

        // ✅ 4. 프롬프트 템플릿 생성
        String promptTemplate = """
                당신은 농업 전문가입니다.
                다음 날씨 데이터를 기반으로 조언을 제공하세요.
                위치: {city}
                날씨: {weatherDescription}
                기온: {temperature}°C
                작물: {cropName}
                """;

        // ✅ 5. AI 호출
        String aiResponse = String.valueOf(openAiService.getRecommendation(
                promptTemplate,
                Map.of(
                        "city", weather.getLocation().getName(),
                        "weatherDescription", weather.getCurrent().getCondition().getText(),
                        "temperature", weather.getCurrent().getTemp_c(),
                        "cropName", cropName
                )
        ));

        return AiRecommendationResponse.builder()
                .answer(aiResponse)
                .build();
    }
}
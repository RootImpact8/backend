package com.example.rootimpact.domain.farm.service;
/**
import com.example.rootimpact.domain.farm.dto.AiRecommendationResponse;
import com.example.rootimpact.domain.farm.dto.WeatherResponse;
import com.example.rootimpact.domain.user.entity.User;
import com.example.rootimpact.domain.userInfo.entity.UserLocation;
import com.example.rootimpact.domain.userInfo.service.UserInfoService;
import com.example.rootimpact.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiRecommendationService {

    private final OpenAiService openAiService;
    private final WeatherService weatherService;
    private final UserInfoService userInfoService;
    private final UserRepository userRepository;

    public AiRecommendationResponse getRecommendationForCrop(String cropName, Authentication authentication) {
        // ✅ 1️⃣ 사용자 정보 가져오기
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ 2️⃣ 사용자 위치 정보 가져오기
        UserLocation userLocation = userInfoService.getUserLocation(user.getId());
        if (userLocation == null) {
            throw new RuntimeException("User location not found");
        }

        // ✅ 3️⃣ 날씨 데이터 가져오기
        WeatherResponse weatherResponse = weatherService.getWeather(authentication);

        // ✅ 4️⃣ AI 프롬프트 생성
        String promptTemplate = """
                당신은 최고의 농업 전문가입니다.
                현재 위치의 날씨 데이터를 기반으로 작물 {cropName}의 최적 관리법과 재배 팁을 제공합니다.
                
                🌎 위치: {city}, {state}
                🌤️ 날씨 상태: {weatherDescription}
                🌡️ 기온: {temperature}°C
                💧 습도: {humidity}%
                
                성장 단계에 따른 주요 활동과 주의점을 설명하세요.
                """;

        // ✅ 5️⃣ AI 요청에 필요한 변수 설정
        Map<String, Object> variables = Map.of(
                "cropName", cropName,
                "city", userLocation.getCity(),
                "state", userLocation.getState(),
                "weatherDescription", weatherResponse.getCurrent().getCondition().getText(),
                "temperature", weatherResponse.getCurrent().getTemp_c(),
                "humidity", weatherResponse.getCurrent().getHumidity()
        );

        // ✅ 6️⃣ OpenAI 서비스 호출
        String aiResponse = openAiService.getRecommendation(promptTemplate, variables);

        // ✅ 7️⃣ 응답 반환
        return AiRecommendationResponse.builder()
                .answer(aiResponse)
                .build();
    }

}
 **/
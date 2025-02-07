package com.example.rootimpact.domain.farm.service;

import com.example.rootimpact.domain.farm.dto.WeatherResponse;
import com.example.rootimpact.domain.user.entity.User;
import com.example.rootimpact.domain.userInfo.entity.UserLocation;
import com.example.rootimpact.domain.userInfo.service.UserInfoService;
import com.example.rootimpact.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class WeatherService {

    private final String API_KEY = "";//"a42b1a756c394b6eb03132303250202"; // Weather API 키
    private final String BASE_URL = "http://api.weatherapi.com/v1/forecast.json"; // Weather API URL
    private final UserInfoService userInfoService;
    private final UserRepository userRepository;
    private final KakaoGeocodingService kakaoGeocodingService;

    public WeatherResponse getWeather(Authentication authentication) {
        String userEmail = authentication.getName();


        // 사용자 정보 가져오기
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("🚨 User not found"));


        UserLocation userLocation = userInfoService.getUserLocation(user.getId());
        if (userLocation == null) {

            throw new RuntimeException("User location not found");
        }

        // ✅ 사용자 거주지 정보를 "시 + 도" 형식으로 변환
        String fullAddress = String.format("%s %s", userLocation.getCity(), userLocation.getState());


        // ✅ KakaoGeocodingService를 호출하여 위도/경도 가져오기
        Map<String, Double> coordinates = kakaoGeocodingService.getCoordinates(fullAddress);


        // Weather API 호출
        String url = String.format(
                "%s?key=%s&q=%f,%f&days=5&aqi=no&alerts=no",
                BASE_URL, API_KEY, coordinates.get("lat"), coordinates.get("lng")
        );



        RestTemplate restTemplate = new RestTemplate();
        try {
            WeatherResponse response = restTemplate.getForObject(url, WeatherResponse.class);

            return response;
        } catch (Exception e) {

            throw new RuntimeException("Failed to fetch weather data: " + e.getMessage());
        }
    }
}
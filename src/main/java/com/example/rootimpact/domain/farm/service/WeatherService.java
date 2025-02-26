package com.example.rootimpact.domain.farm.service;

import com.example.rootimpact.domain.farm.dto.WeatherResponse;
import com.example.rootimpact.domain.user.entity.User;
import com.example.rootimpact.domain.userInfo.entity.UserLocation;
import com.example.rootimpact.domain.userInfo.service.UserInfoService;
import com.example.rootimpact.domain.user.repository.UserRepository;
import com.example.rootimpact.global.error.ErrorCode;
import com.example.rootimpact.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class WeatherService {

    private final String API_KEY ="";//"a42b1a756c394b6eb03132303250202"; // Weather API 키
    private final String BASE_URL ="";// "http://api.weatherapi.com/v1/forecast.json"; // Weather API URL
    private final UserInfoService userInfoService;
    private final UserRepository userRepository;
    private final KakaoGeocodingService kakaoGeocodingService;
    private final RestTemplate restTemplate;

    public WeatherResponse getWeather(Authentication authentication) {
        String userEmail = authentication.getName();

        // 사용자 정보 가져오기
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_USER));


        UserLocation userLocation = userInfoService.getUserLocation(user.getId());
        if (userLocation == null) {

            throw new GlobalException(ErrorCode.NOT_FOUND_USER_LOCATION);
        }

        // 사용자 거주지 정보를 "시 + 도" 형식으로 변환
        String fullAddress = String.format("%s %s", userLocation.getCity(), userLocation.getState());


        // KakaoGeocodingService를 호출하여 위도/경도 가져오기
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

            throw new GlobalException(ErrorCode.FAILED_FETCH_WEATHER, e.getMessage());
        }
    }

    public WeatherResponse getWeatherByUserId(Long userId) {
        // 1. 사용자 정보 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_USER));

        // 2. 사용자 위치 정보 조회
        UserLocation userLocation = userInfoService.getUserLocation(user.getId());
        if (userLocation == null) {
            throw new GlobalException(ErrorCode.NOT_FOUND_USER_LOCATION);
        }

        // 3. 사용자 거주지 정보를 "시 + 도" 형식으로 변환
        String fullAddress = String.format("%s %s", userLocation.getCity(), userLocation.getState());

        // 4. KakaoGeocodingService를 호출하여 위도/경도 가져오기
        Map<String, Double> coordinates = kakaoGeocodingService.getCoordinates(fullAddress);

        // 5. Weather API 호출 URL 생성
        String url = String.format(
                "%s?key=%s&q=%f,%f&days=5&aqi=no&alerts=no",
                BASE_URL, API_KEY, coordinates.get("lat"), coordinates.get("lng")
        );

        // 6. API 호출 및 응답 반환
        try {
            return restTemplate.getForObject(url, WeatherResponse.class);
        } catch (Exception e) {
            throw new GlobalException(ErrorCode.FAILED_FETCH_WEATHER, e.getMessage());
        }
    }
}
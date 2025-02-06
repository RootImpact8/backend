package com.example.rootimpact.domain.farm.controller;

import com.example.rootimpact.domain.farm.dto.AiNewsResponse;
import com.example.rootimpact.domain.farm.dto.AiRecommendationResponse;
import com.example.rootimpact.domain.farm.dto.KamisPriceResponse;
import com.example.rootimpact.domain.farm.dto.WeatherResponse;
import com.example.rootimpact.domain.farm.service.AiNewsService;
import com.example.rootimpact.domain.farm.service.AiRecommendationService;
import com.example.rootimpact.domain.farm.service.KamisPriceService;
import com.example.rootimpact.domain.farm.service.WeatherService;
import com.example.rootimpact.domain.user.entity.User;
import com.example.rootimpact.domain.userInfo.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/farm")
public class FarmController {

    private final AiRecommendationService aiRecommendationService;
    private final AiNewsService aiNewsService;
    private final WeatherService weatherService;
    private final UserInfoService userInfoService;
    private final KamisPriceService kamisPriceService;

    // ✅ 작물 가격 정보 조회
    @GetMapping("/crop-price")
    public ResponseEntity<KamisPriceResponse> getCropPrice(
            @RequestParam String cropName,
            Authentication authentication
    ) {
        KamisPriceResponse response = kamisPriceService.getCropPriceInfo(authentication, cropName);
        return ResponseEntity.ok(response);
    }

    // ✅ 1️⃣ 날씨 정보 요청
    @GetMapping("/weather")
    public ResponseEntity<WeatherResponse> getWeather(Authentication authentication) {
        System.out.println("🔍 WeatherController: /api/weather 요청 수신됨!");
        WeatherResponse weatherResponse = weatherService.getWeather(authentication);
        return ResponseEntity.ok(weatherResponse);
    }

    // ✅ 2️⃣ 관심 작물에 대한 뉴스 요청
    @GetMapping("/crop-news")
    public ResponseEntity<AiNewsResponse> getCropNews(
            @RequestParam String cropName,
            Authentication authentication
    ) {
        String userEmail = authentication.getName();
        User user = userInfoService.getUserByEmail(userEmail);

        // 관심 작물인지 확인
        userInfoService.getSpecificInterestCrop(user.getId(), cropName);

        // AI를 이용해 작물 뉴스 제공
        AiNewsResponse response = aiNewsService.getCropNews(cropName);
        return ResponseEntity.ok(response);
    }



    // ✅ 4️⃣ 재배 작물에 대한 AI 추천 활동 요청
    @GetMapping("/recommendation/cultivated-crop")
    public ResponseEntity<AiRecommendationResponse> getCultivatedCropRecommendation(
            @RequestParam String cropName,
            Authentication authentication
    ) {
        String userEmail = authentication.getName();
        User user = userInfoService.getUserByEmail(userEmail);

        // 재배 작물인지 확인
        userInfoService.getSpecificCultivatedCrop(user.getId(), cropName);

        // AI 추천 요청
        AiRecommendationResponse response = aiRecommendationService.getRecommendationForCrop(cropName, authentication);
        return ResponseEntity.ok(response);
    }
}
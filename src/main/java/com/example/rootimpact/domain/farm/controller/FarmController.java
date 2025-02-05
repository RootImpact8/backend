package com.example.rootimpact.domain.farm.controller;

import com.example.rootimpact.domain.farm.dto.AiNewsResponse;
import com.example.rootimpact.domain.farm.dto.AiRecommendationResponse;
import com.example.rootimpact.domain.farm.dto.WeatherResponse;
import com.example.rootimpact.domain.farm.service.AiNewsService;
import com.example.rootimpact.domain.farm.service.AiRecommendationService;
import com.example.rootimpact.domain.farm.service.WeatherService;
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


    @GetMapping("/weather")
    public WeatherResponse getWeather(Authentication authentication) {
        System.out.println("🔍 WeatherController: /api/weather 요청 수신됨!");
        return weatherService.getWeather(authentication);
    }

    // ✅ 2️⃣ AI 추천 활동 요청
    @GetMapping("/recommendation")
    public ResponseEntity<AiRecommendationResponse> getRecommendation(Authentication authentication) {
        AiRecommendationResponse response = aiRecommendationService.getRecommendation(authentication);
        return ResponseEntity.ok(response);
    }

    // ✅ 3️⃣ AI 작물 관련 뉴스 요청
    @GetMapping("/crop-news")
    public ResponseEntity<AiNewsResponse> getCropNews(@RequestParam String cropName) {
        AiNewsResponse response = aiNewsService.getCropNews(cropName);
        return ResponseEntity.ok(response);
    }
}
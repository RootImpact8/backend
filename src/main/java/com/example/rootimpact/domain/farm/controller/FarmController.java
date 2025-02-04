package com.example.rootimpact.domain.farm.controller;



import com.example.rootimpact.domain.farm.dto.AiRecommendationResponse;
import com.example.rootimpact.domain.farm.dto.WeatherResponse;
import com.example.rootimpact.domain.farm.service.AiRecommendationService;
import com.example.rootimpact.domain.farm.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/farm")
public class FarmController {

    private final AiRecommendationService aiService;
    private final WeatherService weatherService;

    // 날씨 데이터 요청
    @GetMapping("/weather")
    public ResponseEntity<WeatherResponse> getWeather(@RequestParam String city) {
        WeatherResponse response = weatherService.getWeather(city);
        return ResponseEntity.ok(response);
    }

    // AI 추천 활동 요청
    @GetMapping("/recommendation")
    public ResponseEntity<AiRecommendationResponse> getRecommendation(@RequestParam String city) {
        AiRecommendationResponse response = aiService.getRecommendation(city);
        return ResponseEntity.ok(response);
    }
}

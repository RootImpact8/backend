package com.example.rootimpact.domain.farm.controller;

import com.example.rootimpact.domain.farm.dto.AiNewsResponse;
import com.example.rootimpact.domain.farm.dto.AiRecommendationResponse;
import com.example.rootimpact.domain.farm.dto.KamisPriceResponse;
import com.example.rootimpact.domain.farm.dto.WeatherResponse;
import com.example.rootimpact.domain.farm.service.AiNewsService;
import com.example.rootimpact.domain.farm.service.FarmActivateService;
import com.example.rootimpact.domain.farm.service.KamisPriceService;
import com.example.rootimpact.domain.farm.service.WeatherService;
import com.example.rootimpact.domain.user.entity.User;
import com.example.rootimpact.domain.userInfo.service.UserInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/farm")
@Tag(name = "Farm", description = "농업 ai 관련 API")
public class FarmController {

    private final AiNewsService aiNewsService;
    private final WeatherService weatherService;
    private final UserInfoService userInfoService;
    private final KamisPriceService kamisPriceService;
    private final FarmActivateService farmActivateService;

    @Operation(
            summary = "AI 기반 재배 추천",
            description = "사용자 ID와 작물명과 작물일기 데이터 기반으로 AI 추천 결과(작물 재배 일차 등)를 반환합니다."
    )
    @GetMapping("/ai-recommendation")
    public ResponseEntity<AiRecommendationResponse> getAiRecommendation(
            @Parameter(description = "사용자 ID", required = true, example = "1")
            @RequestParam Long userId,
            @Parameter(description = "작물명", required = true, example = "Tomato")
            @RequestParam String cropName) {
        AiRecommendationResponse recommendation = farmActivateService.getAiRecommendation(userId, cropName);
        return ResponseEntity.ok(recommendation);
    }

    @Operation(
            summary = "도매시장 실시간 경락가 조회",
            description = "작물명을 기반으로 도매시장에서의 실시간 경락가 정보를 반환합니다."
    )
    @GetMapping("/api/farm/price")
    public ResponseEntity<KamisPriceResponse> getCropPrice(
            @Parameter(description = "작물명", required = true, example = "Apple")
            @RequestParam String cropName,
            @Parameter(hidden = true)
            Authentication authentication) {
        KamisPriceResponse response = kamisPriceService.getPriceInfo(cropName, authentication);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "날씨 정보 요청",
            description = "현재 인증된 사용자의 날씨 정보 및 추후5일까지 날씨정보 반환합니다."
    )
    @GetMapping("/weather")
    public ResponseEntity<WeatherResponse> getWeather(
            @Parameter(hidden = true)
            Authentication authentication) {

        WeatherResponse weatherResponse = weatherService.getWeather(authentication);
        return ResponseEntity.ok(weatherResponse);
    }

    @Operation(
            summary = "관심 작물 뉴스 조회",
            description = "관심 작물에 대한 AI 기반 뉴스 및 주요 정보를 반환합니다."
    )
    @GetMapping("/crop-news")
    public ResponseEntity<AiNewsResponse> getCropNews(
            @Parameter(description = "작물명", required = true, example = "Corn")
            @RequestParam String cropName,
            @Parameter(hidden = true)
            Authentication authentication) {
        String userEmail = authentication.getName();
        User user = userInfoService.getUserByEmail(userEmail);

        // 관심 작물 여부 확인 (예외 발생 시 처리)
        userInfoService.getSpecificInterestCrop(user.getId(), cropName);

        AiNewsResponse response = aiNewsService.getCropNews(cropName);
        return ResponseEntity.ok(response);
    }
}
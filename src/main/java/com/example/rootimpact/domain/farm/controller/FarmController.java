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
import com.example.rootimpact.global.error.ErrorResponse;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/farm")
@Tag(name = "Farm", description = "농업 AI 관련 API")
public class FarmController {

    private final AiNewsService aiNewsService;
    private final WeatherService weatherService;
    private final UserInfoService userInfoService;
    private final KamisPriceService kamisPriceService;
    private final FarmActivateService farmActivateService;

    @GetMapping("/user-crops/price")
    public ResponseEntity<List<KamisPriceResponse>> getUserCropsPrice(
            @RequestParam(name = "userId") Long userId) {
        List<KamisPriceResponse> priceResponses = kamisPriceService.getUserCropsPriceInfo(userId);
        return ResponseEntity.ok(priceResponses);
    }

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

    @GetMapping("/price")
    public ResponseEntity<?> getCropPrice(
            @RequestParam(name = "cropName", required = true) String cropName) {
        try {
            if (!StringUtils.hasText(cropName)) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("작물명은 필수 입력값입니다."));
            }

            if (!isSupportedCrop(cropName)) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("지원하지 않는 작물입니다: " + cropName));
            }

            KamisPriceResponse response = kamisPriceService.getPriceInfo(cropName);

            if (response.getPreviousPrice() == null || response.getCurrentPrice() == null) {
                return ResponseEntity.ok()
                        .body(new ErrorResponse("해당 작물의 가격 정보가 없습니다."));
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("가격 정보 조회 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("서버 오류가 발생했습니다."));
        }
    }

    private boolean isSupportedCrop(String cropName) {
        return Arrays.asList("딸기", "쌀", "감자", "상추", "사과", "고추").contains(cropName);
    }

    @Operation(
            summary = "날씨 정보 요청",
            description = "현재 인증된 사용자의 날씨 정보 및 추후 5일까지의 날씨 정보를 반환합니다."
    )
    @GetMapping("/weather")
    public ResponseEntity<WeatherResponse> getWeather(
            @Parameter(hidden = true) Authentication authentication) {
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
            @Parameter(hidden = true) Authentication authentication) {
        String userEmail = authentication.getName();
        User user = userInfoService.getUserByEmail(userEmail);

        userInfoService.getSpecificInterestCrop(user.getId(), cropName);

        AiNewsResponse response = aiNewsService.getCropNews(cropName);
        return ResponseEntity.ok(response);
    }
}
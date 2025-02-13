package com.example.rootimpact.domain.farm.controller;

import com.example.rootimpact.domain.farm.dto.AiNewsResponse;
import com.example.rootimpact.domain.farm.dto.AiRecommendationResponse;
import com.example.rootimpact.domain.farm.dto.KamisPriceResponse;
import com.example.rootimpact.domain.farm.dto.RdaVarietyResponse;
import com.example.rootimpact.domain.farm.dto.WeatherResponse;
import com.example.rootimpact.domain.farm.service.AiNewsService;
import com.example.rootimpact.domain.farm.service.FarmActivateService;
import com.example.rootimpact.domain.farm.service.KamisPriceService;
import com.example.rootimpact.domain.farm.service.RdaVarietyService;
import com.example.rootimpact.domain.farm.service.WeatherService;
import com.example.rootimpact.domain.user.entity.User;
import com.example.rootimpact.domain.userInfo.entity.UserCrop;
import com.example.rootimpact.domain.userInfo.service.UserInfoService;
import com.example.rootimpact.global.error.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    private final RdaVarietyService rdaVarietyService;

    // 사용자 관심 작물 품종 조회
    @Operation(summary = "사용자 관심 작물 품종 조회(개별)", description = "사용자의 관심 작물에 대한 품종 반환")
    @GetMapping("/variety")
    public ResponseEntity<List<RdaVarietyResponse>> getVarietyByUserCropId(
            @Parameter(description = "사용자 ID", required = true, example = "1")
            @RequestParam(name = "userId") Long userId,
            @Parameter(description = "작물 ID", required = true, example = "1")
            @RequestParam(name = "cropId") Long cropId) {
        return ResponseEntity.ok(rdaVarietyService.getVarietyByUserCropId(userId, cropId));
    }

    // 작물 품종 조회
    @Operation(summary = "작물 품종 조회", description = "작물 ID에 대한 작물 품종 반환")
    @GetMapping("/variety/{cropId}")
    public ResponseEntity<List<RdaVarietyResponse>> getVariety(
            @Parameter(description = "작물 ID", required = true, example = "1")
            @PathVariable("cropId") Long cropId) {
        return ResponseEntity.ok(rdaVarietyService.getRdaVarietyListByCropId(cropId));
    }

    // 사용자 재배 작물 가격 비교 (사용자 ID 기반)
    @Operation(summary = "유저 전체 작물 가격정보 비교", description = "사용자의 재배 작물에 대한 실시간 가격 정보를 가져와 전날과 비교하여 가격 비교 반환")
    @GetMapping("/user-crops/price")
    public ResponseEntity<List<KamisPriceResponse>> getUserCropsPrice(
            @Parameter(description = "사용자 ID", required = true, example = "1")
            @RequestParam(name = "userId") Long userId) {
        List<KamisPriceResponse> priceResponses = kamisPriceService.getUserCropsPriceInfo(userId);
        return ResponseEntity.ok(priceResponses);
    }

    @Operation(summary = "AI 기반 재배 추천", description = "사용자 ID와 작물 ID 및 작물 일기 데이터를 기반으로 AI 추천 결과를 반환합니다.")
    @GetMapping("/ai-recommendation")
    public ResponseEntity<?> getAiRecommendation(
            @Parameter(description = "사용자 ID", required = true, example = "1") @RequestParam Long userId,
            @Parameter(description = "작물 ID", required = true, example = "3") @RequestParam Long cropId) {

        return farmActivateService.getAiRecommendation(userId, cropId);
    }

    // 작물 가격 조회 (작물 ID 기반)
    @Operation(summary = "작물 가격정보", description = "사용자와 상관없이 특정 작물의 가격 정보를 검색하면 가격을 반환합니다.")
    @GetMapping("/price")
    public ResponseEntity<?> getCropPrice(
            @Parameter(description = "작물 ID", required = true, example = "2")
            @RequestParam(name = "cropId", required = true) Long cropId) {
        try {
            KamisPriceResponse response = kamisPriceService.getPriceInfo(cropId);

            if (response.getPriceData() == null) {
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

    // 날씨 정보 조회
    @Operation(summary = "날씨 정보 요청", description = "현재 인증된 사용자의 날씨 정보 및 추후 5일까지의 날씨 정보를 반환합니다.")
    @GetMapping("/weather")
    public ResponseEntity<WeatherResponse> getWeather(
            @Parameter(hidden = true) Authentication authentication) {
        WeatherResponse weatherResponse = weatherService.getWeather(authentication);
        return ResponseEntity.ok(weatherResponse);
    }

    // 관심 작물 뉴스 조회 (작물 ID 기반)
    @Operation(summary = "관심 작물 뉴스 조회", description = "관심 작물에 대한 AI 기반 뉴스 및 주요 정보를 반환합니다.")
    @GetMapping("/crop-news")
    public ResponseEntity<?> getCropNews(
            @Parameter(description = "작물 ID", required = true, example = "4")
            @RequestParam Long cropId,
            @Parameter(hidden = true) Authentication authentication) {
        try {
            // 사용자 인증 정보에서 이메일 가져오기
            String userEmail = authentication.getName();
            User user = userInfoService.getUserByEmail(userEmail);

            // 사용자 관심 작물인지 확인
            UserCrop userCrop = userInfoService.getSpecificInterestCrop(user.getId(), cropId);

            if (userCrop == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorResponse("해당 작물은 관심 작물에 등록되어 있지 않습니다."));
            }

            // ✅ AI 뉴스 제공
            AiNewsResponse response = aiNewsService.getCropNews(user.getId(), cropId);
          
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("관심 작물 뉴스 조회 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("서버 오류가 발생했습니다."));
        }
    }
}
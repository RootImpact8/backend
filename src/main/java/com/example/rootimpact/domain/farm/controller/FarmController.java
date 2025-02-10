package com.example.rootimpact.domain.farm.controller;

import com.example.rootimpact.domain.farm.dto.AiNewsResponse;
import com.example.rootimpact.domain.farm.dto.KamisPriceResponse;
import com.example.rootimpact.domain.farm.dto.WeatherResponse;
import com.example.rootimpact.domain.farm.service.AiNewsService;
import com.example.rootimpact.domain.farm.service.KamisPriceService;
import com.example.rootimpact.domain.farm.service.WeatherService;
import com.example.rootimpact.domain.user.entity.User;
import com.example.rootimpact.domain.userInfo.service.UserInfoService;
import com.example.rootimpact.global.error.ErrorResponse;
import java.util.Arrays;
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
public class FarmController {

    //private final AiRecommendationService aiRecommendationService;
    private final AiNewsService aiNewsService;
    private final WeatherService weatherService;
    private final UserInfoService userInfoService;
    private final KamisPriceService kamisPriceService;




    // 작물 가격 조회 & 변동률, 가격 변동 상태
    @GetMapping("/price")
    public ResponseEntity<?> getCropPrice(
            @RequestParam(name = "cropName", required = true) String cropName
    ) {
        try {
            // 작물명 유효성 검사
            if (!StringUtils.hasText(cropName)) {
                return ResponseEntity.badRequest()
                               .body(new ErrorResponse("작물명은 필수 입력값입니다."));
            }

            // 지원하는 작물인지 확인
            if (!isSupportedCrop(cropName)) {
                return ResponseEntity.badRequest()
                               .body(new ErrorResponse("지원하지 않는 작물입니다: " + cropName));
            }

            // KAMIS API를 통해 작물 가격 정보 조회
            KamisPriceResponse response = kamisPriceService.getPriceInfo(cropName);

            // 조회된 데이터가 없는 경우
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

    // 지원하는 작물 확인
    private boolean isSupportedCrop(String cropName) {
        return Arrays.asList("딸기", "쌀", "감자", "상추", "사과", "고추").contains(cropName);
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


    /*
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
     */
}
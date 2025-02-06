package com.example.rootimpact.domain.farm.service;

import com.example.rootimpact.domain.farm.dto.KamisPriceResponse;
import com.example.rootimpact.domain.user.entity.User;
import com.example.rootimpact.domain.user.repository.UserRepository;
import com.example.rootimpact.domain.userInfo.entity.UserCrop;
import com.example.rootimpact.domain.userInfo.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class KamisPriceService {

    private final UserInfoService userInfoService;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    private final String MARKET_PRICE_URL = "https://at.agromarket.kr/openApi/price/real.do";
    private final String API_KEY = "E279E46D7A1D4D85918EA25AAA6936B3"; // 발급받은 키

    public KamisPriceResponse getPriceInfo(String cropName, Authentication authentication) {
        String userEmail = authentication.getName();

        // 사용자 정보 조회
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 사용자의 재배 작물 정보 가져오기
        UserCrop userCrop = userInfoService.getSpecificCultivatedCrop(user.getId(), cropName);

        // API 요청 URL 생성
        String url = generateMarketPriceUrl(cropName);

        // API 호출
        return fetchMarketPrice(url, cropName);
    }

    private String generateMarketPriceUrl(String cropName) {
        return MARKET_PRICE_URL +
                "?serviceKey=" + API_KEY +
                "&apiType=json" +
                "&pageNo=1" +
                "&whsalCd=110001" + // 서울 가락시장 코드
                "&midCd=" + mapCropToMidCd(cropName); // 중분류 코드 매핑
    }

    private String mapCropToMidCd(String cropName) {
        // 도매시장 API에 따른 작물 중분류 코드 매핑
        return switch (cropName) {
            case "감자" -> "01"; // 감자 중분류 코드
            case "고구마" -> "02"; // 고구마 중분류 코드
            case "쌀" -> "03"; // 쌀 중분류 코드
            default -> "00"; // 기본값 (조회 불가능한 경우)
        };
    }

    private KamisPriceResponse fetchMarketPrice(String url, String cropName) {
        try {
            // API 응답 데이터 처리
            var response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.containsKey("data")) {
                Map<String, Object> data = (Map<String, Object>) response.get("data");

                return KamisPriceResponse.builder()
                        .cropName(cropName)
                        .wholesaleMarketName(data.get("whsalName").toString())
                        .saleDate(data.get("saleDate").toString())
                        .price(data.get("price").toString())
                        .build();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch market price: " + e.getMessage());
        }
        throw new RuntimeException("No market price data available");
    }
}
package com.example.rootimpact.domain.farm.service;

import com.example.rootimpact.domain.farm.dto.KamisPriceResponse;
import com.example.rootimpact.domain.user.entity.User;
import com.example.rootimpact.domain.userInfo.entity.UserLocation;
import com.example.rootimpact.domain.userInfo.service.UserInfoService;
import com.example.rootimpact.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KamisPriceService {

    private static final String API_KEY = "";//"a994e8f5-ce25-494c-8f9b-d12b77b0c8e4";
    private static final String BASE_URL = "https://www.kamis.or.kr/service/price/xml.do?action=dailySalesList";
    private final UserRepository userRepository;
    private final UserInfoService userInfoService;

    public KamisPriceResponse getCropPriceInfo(Authentication authentication, String cropName) {
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ 사용자 지역 정보 가져오기
        UserLocation userLocation = userInfoService.getUserLocation(user.getId());

        // ✅ 날짜 설정 (전날, 당일)
        String today = LocalDate.now().toString().replace("-", "");
        String yesterday = LocalDate.now().minusDays(1).toString().replace("-", "");

        // ✅ KAMIS API 요청 URL 생성
        String url = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .queryParam("p_cert_key", API_KEY)
                .queryParam("p_returntype", "json")
                .queryParam("p_startday", yesterday)
                .queryParam("p_endday", today)
                .queryParam("p_countycode", getRegionCode(userLocation.getCity())) // 지역 코드 매핑
                .build().toString();

        // ✅ API 호출
        RestTemplate restTemplate = new RestTemplate();
        KamisPriceResponse response = restTemplate.getForObject(url, KamisPriceResponse.class);

        // ✅ 사용자가 선택한 작물 필터링
        List<KamisPriceResponse.PriceInfo> filteredPrices = response.getPriceList().stream()
                .filter(price -> price.getItemName().equalsIgnoreCase(cropName))
                .collect(Collectors.toList());

        // ✅ 최종 응답 반환
        return new KamisPriceResponse(filteredPrices);
    }

    private String getRegionCode(String cityName) {
        return switch (cityName) {
            case "서울특별시" -> "1101";
            case "부산광역시" -> "2100";
            case "대구광역시" -> "2200";
            case "인천광역시" -> "2300";
            case "광주광역시" -> "2400";
            case "대전광역시" -> "2500";
            case "울산광역시" -> "2600";
            case "세종특별자치시" -> "2700";
            default -> "1101"; // 기본값 (서울)
        };
    }
}

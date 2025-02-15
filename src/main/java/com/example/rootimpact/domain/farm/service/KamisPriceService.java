package com.example.rootimpact.domain.farm.service;

import com.example.rootimpact.domain.farm.dto.CropInfo;
import com.example.rootimpact.domain.farm.dto.KamisPriceResponse;
import com.example.rootimpact.domain.farm.dto.PriceData;
import com.example.rootimpact.domain.farm.type.CropType;
import com.example.rootimpact.domain.farm.util.DateUtils;
import com.example.rootimpact.domain.userInfo.entity.UserCrop;
import com.example.rootimpact.domain.userInfo.service.UserInfoService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class KamisPriceService {

    private final RestTemplate restTemplate;

    private final String CERT_KEY ="";// "a994e8f5-ce25-494c-8f9b-d12b77b0c8e4"; //발급받은 키
    private final String CERT_ID = "";//"5266"; //발급받은 ID

    private final UserInfoService userInfoService;

    // 사용자 재배 작물들의 가격 정보 조회
    public List<KamisPriceResponse> getUserCropsPriceInfo(Long userId) {
        // 사용자의 재배 작물 목록 조회
        List<UserCrop> cultivatedCrops = userInfoService.getCultivatedCrops(userId);

        // 각 작물별 가격 정보 조회 -> 리스트로 변환
        return cultivatedCrops.stream()
                .map(userCrop -> {
                    try {
                        // cropId로 가격 정보 조회
                        return getPriceInfo(userCrop.getCropId());
                    } catch (Exception e) {
                        log.error("작물 ID {} 가격 조회 실패: {}", userCrop.getCropId(), e.getMessage());
                        // 에러 발생 시 해당 작물에 대한 빈 응답 반환
                        return createEmptyResponse(userCrop.getCropId());
                    }
                })
                .collect(Collectors.toList());
    }

    // 작물 가격 조회
    public KamisPriceResponse getPriceInfo(Long cropId) {
        String url = generateKamisApiUrl(cropId);
        HttpHeaders headers = createHeaders();
        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            log.info("Response status: {}", response.getStatusCode());
            return processResponse(response.getBody(), cropId);
        } catch (Exception e) {
            log.error("API 호출 실패: {}", e.getMessage());
            throw new RuntimeException("가격 조회 실패: " + e.getMessage());
        }
    }

    // HTTP 요청 헤더 생성
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN));
        headers.set("User-Agent", "Mozilla/5.0");
        return headers;
    }

    // KAMIS API 요청 URL 생성
    private String generateKamisApiUrl(Long cropId) {

        CropInfo cropInfo = CropType.getInfoById(cropId);

        return UriComponentsBuilder.fromPath("/service/price/xml.do")
                .scheme("http")
                .host("www.kamis.or.kr")
                .queryParam("action", "periodProductList")
                .queryParam("p_cert_key", CERT_KEY)
                .queryParam("p_cert_id", CERT_ID)
                .queryParam("p_returntype", "json")
                .queryParam("p_startday", DateUtils.getPreviousDate())
                .queryParam("p_endday", DateUtils.getCurrentDate())
                .queryParam("p_productclscode", "02") // 도매
                .queryParam("p_itemcategorycode", cropInfo.getCategoryCode())
                .queryParam("p_itemcode", cropInfo.getItemCode())
                .queryParam("p_kindcode", cropInfo.getKindCode())
                .queryParam("p_productrankcode", "04")
                .queryParam("p_countrycode", "1101") // 서울
                .queryParam("p_convert_kg_yn", "Y")
                .build()
                .toUriString();
    }

    // 응답을 원하는 데이터로 변경
    private KamisPriceResponse processResponse(String responseBody, Long cropId) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(responseBody);

            log.info("API Response Body: {}", responseBody);

            // data 노드가 배열이고 "001"이 포함된 경우 오류로 처리
            if (rootNode.has("data") && rootNode.get("data").isArray()
                    && rootNode.get("data").size() == 1
                    && "001".equals(rootNode.get("data").get(0).asText())) {
                log.warn("API 오류 응답 (001): {}", responseBody);
                return createEmptyResponse(cropId);
            }

            // data 노드 확인 및 error_code 체크
            if (!rootNode.has("data") || !rootNode.get("data").has("item")) {
                log.warn("데이터가 없습니다: {}", responseBody);
                return createEmptyResponse(cropId);
            }

            JsonNode items = rootNode.get("data").get("item");
            List<PriceData> priceList = new ArrayList<>();

            // cropId로 CropInfo 조회하여 itemName 가져오기
            String itemName = CropType.getInfoById(cropId).getItemName();

            // 가격 정보 수집
            for (JsonNode item : items) {
                if (item.has("countyname") && "서울".equals(item.get("countyname").asText())) {
                    try {
                        if (item.has("regday") && item.has("price") && item.has("yyyy")) {
                            String yyyy = item.get("yyyy").asText();
                            String regday = yyyy + "-" + item.get("regday").asText().replace("/", "-");
                            String priceStr = item.get("price").asText("0");

                            try {
                                int price = Integer.parseInt(priceStr.replaceAll("[^0-9]", ""));
                                priceList.add(new PriceData(regday, price));
                            } catch (NumberFormatException e) {
                                log.warn("가격 변환 실패 - 작물 ID: {}, 가격: {}", cropId, priceStr);
                            }
                        }
                    } catch (Exception e) {
                        log.warn("데이터 처리 중 오류 발생: {}", e.getMessage());
                    }
                }
            }

            if (priceList.isEmpty()) {
                return createEmptyResponse(cropId);
            }

            priceList.sort(Comparator.comparing(PriceData::getDate));
            return new KamisPriceResponse(itemName, priceList);

        } catch (Exception e) {
            log.error("응답 처리 실패: {}", e.getMessage());
            throw new RuntimeException("응답 처리 실패: " + e.getMessage());
        }
    }

    // 응답이 없을 때
    private KamisPriceResponse createEmptyResponse(Long cropId) {
        String itemName = CropType.getInfoById(cropId).getItemName();
        return new KamisPriceResponse(itemName, new ArrayList<>());
    }




}
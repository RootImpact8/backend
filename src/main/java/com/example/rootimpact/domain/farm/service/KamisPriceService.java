package com.example.rootimpact.domain.farm.service;

import com.example.rootimpact.domain.farm.dto.CropInfo;
import com.example.rootimpact.domain.farm.dto.KamisPriceResponse;
import com.example.rootimpact.domain.farm.dto.PriceInfo;
import com.example.rootimpact.domain.farm.dto.RegionPriceInfo;
import com.example.rootimpact.domain.farm.type.CropType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private final String CERT_KEY = ""; //"a994e8f5-ce25-494c-8f9b-d12b77b0c8e4" 발급받은 키
    private final String CERT_ID = ""; // "5266" 발급받은 ID

    // 작물 가격 조회
    public KamisPriceResponse getPriceInfo(String cropName) {
        String url = generateKamisApiUrl(cropName);
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
            return processResponse(response.getBody(), cropName);
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
    private String generateKamisApiUrl(String cropName) {

        LocalDate today = LocalDate.now();
        LocalDate endDay = today.minusDays(1);
        LocalDate startDay = today.minusDays(2);

        CropInfo cropInfo = CropType.getInfoByName(cropName);

        return UriComponentsBuilder.fromPath("/service/price/xml.do")
                       .scheme("http")
                       .host("www.kamis.or.kr")
                       .queryParam("action", "periodProductList")
                       .queryParam("p_cert_key", CERT_KEY)
                       .queryParam("p_cert_id", CERT_ID)
                       .queryParam("p_returntype", "json")
                       .queryParam("p_startday", startDay.format(DateTimeFormatter.ISO_DATE))
                       .queryParam("p_endday", endDay.format(DateTimeFormatter.ISO_DATE))
                       .queryParam("p_productclscode", "02")
                       .queryParam("p_itemcategorycode", cropInfo.getCategoryCode())
                       .queryParam("p_itemcode", cropInfo.getItemCode())
                       .queryParam("p_kindcode", cropInfo.getKindCode())
                       .queryParam("p_productrankcode", "04")
                       .queryParam("p_convert_kg_yn", "Y")
                       .build()
                       .toUriString();
    }

    // API 응답 처리 -> 가격 정보로 변환
    private KamisPriceResponse processResponse(String responseBody, String cropName) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(responseBody);

            log.info("API Response Body: {}", responseBody);

            // data 노드 확인 및 error_code 체크
            if (!rootNode.has("data") || !rootNode.get("data").has("item")) {
                log.warn("데이터가 없습니다: {}", responseBody);
                return createEmptyResponse(cropName);
            }

            JsonNode items = rootNode.get("data").get("item");
            Map<String, PriceInfo> priceByCounty = new HashMap<>();

            // 지역별 가격 정보 수집
            for (JsonNode item : items) {
                try {
                    // 평균, 평년 데이터는 제외
                    String countyName = item.get("countyname").asText();
                    if ("평균".equals(countyName) || "평년".equals(countyName)) {
                        continue;
                    }

                    // 필수 필드가 모두 있는지 확인
                    if (item.has("countyname") && item.has("regday") && item.has("price") && item.has("yyyy")) {
                        String yyyy = item.get("yyyy").asText();
                        String regday = yyyy + "-" + item.get("regday").asText().replace("/", "-");
                        String priceStr = item.get("price").asText("0");

                        // 가격 문자열 정제 및 변환
                        double price = 0.0;
                        try {
                            price = Double.parseDouble(priceStr.replaceAll("[^0-9.]", ""));
                        } catch (NumberFormatException e) {
                            log.warn("가격 변환 실패 - 품목: {}, 가격: {}", cropName, priceStr);
                            continue;
                        }

                        priceByCounty.computeIfAbsent(countyName, k -> new PriceInfo())
                                .addPrice(regday, price);
                    }
                } catch (Exception e) {
                    log.warn("데이터 처리 중 오류 발생: {}", e.getMessage());
                    continue;
                }
            }

            if (priceByCounty.isEmpty()) {
                return createEmptyResponse(cropName);
            }

            return createPriceResponse(cropName, priceByCounty);
        } catch (Exception e) {
            log.error("응답 처리 실패: {}", e.getMessage());
            throw new RuntimeException("응답 처리 실패: " + e.getMessage());
        }
    }

    // 데이터가 없는 경우 -> 빈 응답 생성
    private KamisPriceResponse createEmptyResponse(String cropName) {
        return KamisPriceResponse.builder()
                       .itemName(cropName)
                       .regionPrices(new ArrayList<>())
                       .startDate(LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_DATE))
                       .endDate(LocalDate.now().minusDays(2).format(DateTimeFormatter.ISO_DATE))
                       .build();
    }

    // 수집된 가격 정보 -> 응답 형식으로 변환
    private KamisPriceResponse createPriceResponse(String cropName, Map<String, PriceInfo> priceByCounty) {
        List<RegionPriceInfo> regionPrices = new ArrayList<>();

        LocalDate today = LocalDate.now();
        LocalDate endDay = today.minusDays(1);
        LocalDate startDay = today.minusDays(2);

        String endDayStr = endDay.format(DateTimeFormatter.ISO_DATE);
        String startDayStr = startDay.format(DateTimeFormatter.ISO_DATE);

        // 주말 여부 확인
        boolean isWeekend = endDay.getDayOfWeek() == DayOfWeek.SATURDAY
                                    || endDay.getDayOfWeek() == DayOfWeek.SUNDAY
                                    || startDay.getDayOfWeek() == DayOfWeek.SATURDAY
                                    || startDay.getDayOfWeek() == DayOfWeek.SUNDAY;

        priceByCounty.forEach((countyName, priceInfo) -> {
            Double endDayPrice = priceInfo.getPricesByDate().get(endDayStr);
            Double startDayPrice = priceInfo.getPricesByDate().get(startDayStr);

            // 주말인 날이 있으면 changeRate: null, priceStatus: -
            RegionPriceInfo regionPrice = RegionPriceInfo.builder()
                                                                     .countyName(countyName)
                                                                     .regDay(endDayStr)
                                                                     .startDayPrice(startDayPrice)
                                                                     .endDayPrice(endDayPrice)
                                                                     .changeRate(isWeekend ? null : calculateChangeRate(startDayPrice, endDayPrice))
                                                                     .priceStatus(isWeekend ? "-" : calculatePriceStatus(startDayPrice, endDayPrice))
                                                                     .build();

            regionPrices.add(regionPrice);
        });

        return KamisPriceResponse.builder()
                       .itemName(cropName)
                       .regionPrices(regionPrices)
                       .startDate(startDayStr)
                       .endDate(endDayStr)
                       .build();
    }

    // 가격 변동률 계산
    private Double calculateChangeRate(Double startDayPrice, Double endDayPrice) {
        if (startDayPrice == null || endDayPrice == null || startDayPrice == 0) {
            return null;
        }
        return Math.round(((endDayPrice - startDayPrice) / startDayPrice * 100) * 100.0) / 100.0;
    }

    // 가격 변동 상태 계산
    private String calculatePriceStatus(Double startDayPrice, Double endDayPrice) {
        if (startDayPrice == null || endDayPrice == null) {
            return "-";
        }
        double changeRate = calculateChangeRate(startDayPrice, endDayPrice);
        if (changeRate > 0) {
            return "상승";
        } else if (changeRate < 0) {
            return "하락";
        }
        return "동일";
    }

}
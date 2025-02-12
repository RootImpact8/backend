package com.example.rootimpact.domain.farm.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class KakaoGeocodingService {


    private static final String KAKAO_API_KEY ="f67936837301f598045cf8bb51986262"; // ✅ 본인의 Kakao API 키 사용

    public Map<String, Double> getCoordinates(String address) {


        String apiUrl = String.format(
                "https://dapi.kakao.com/v2/local/search/address.json?query=%s",
                address
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + KAKAO_API_KEY);

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, Map.class);


            if (response.getBody() != null && response.getBody().get("documents") != null) {
                List<Map<String, Object>> documents = (List<Map<String, Object>>) response.getBody().get("documents");
                if (!documents.isEmpty()) {
                    Map<String, Object> firstDocument = documents.get(0);

                    return Map.of(
                            "lat", Double.parseDouble(firstDocument.get("y").toString()),
                            "lng", Double.parseDouble(firstDocument.get("x").toString())
                    );
                }
            }
        } catch (Exception e) {

        }

        throw new RuntimeException("Failed to fetch coordinates for the given address.");
    }
}
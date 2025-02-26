package com.example.rootimpact.domain.farm.service;

import com.example.rootimpact.domain.farm.dto.RdaVarietyResponse;
import com.example.rootimpact.domain.farm.type.CropCategory;
import com.example.rootimpact.domain.userInfo.entity.UserCrop;
import com.example.rootimpact.domain.userInfo.repository.UserCropRepository;
import com.example.rootimpact.global.error.ErrorCode;
import com.example.rootimpact.global.exception.GlobalException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

@Slf4j
@Service
@RequiredArgsConstructor
public class RdaVarietyService {

    private final RestTemplate restTemplate;

    private final String API_KEY = "20250213XTCMNRTFNSMZQCDMEZ5G";
    private final String BASE_URL = "http://api.nongsaro.go.kr/service/cropEbook/varietyList";

    private final UserCropRepository userCropRepository;

    // 사용자 관심 작물별 조회
    public List<RdaVarietyResponse> getVarietyByUserCropId(Long userId, Long cropId) {
        // 사용자의 특정 관심 작물 확인
        UserCrop userCrop = userCropRepository.findByUserIdAndCropIdAndIsInterestCropTrue(userId, cropId)
                                    .orElseThrow(()-> new IllegalArgumentException("Not found interest crop"));

        return getRdaVarietyListByCropId(cropId);
    }

    // 작물 ID로 조회
    public List<RdaVarietyResponse> getRdaVarietyListByCropId(Long cropId) {
        log.info("Requesting variety info for cropId: {}", cropId);

        CropCategory category = Arrays.stream(CropCategory.values())
                                        .filter(c -> c.getId().equals(cropId))
                                        .findFirst()
                                        .orElseThrow(() -> new GlobalException(ErrorCode.INVALID_CROP_NAME, cropId));

        log.info("Found category: {}, code: {}", category.getCropName(), category.getCode());
        return getRdaVarietyList(category);
    }

    // 품종 목록 조회
    public List<RdaVarietyResponse> getRdaVarietyList(CropCategory category) {
        log.info("Fetching variety list for category: {}", category.getCropName());

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                                                            .queryParam("apiKey", API_KEY)
                                                            .queryParam("subCategoryCode", category.getCode());

        String url = uriComponentsBuilder.toUriString();
        log.info("Request URL: {}", url);

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        log.info("Response status: {}", response.getStatusCode());
        log.debug("Response body: {}", response.getBody());
        log.info("Full XML Response: {}", response.getBody());

        List<RdaVarietyResponse> result = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(response.getBody()));
            Document document = builder.parse(is);

            NodeList itemList = document.getElementsByTagName("item");
            log.info("Found {} items in response", itemList.getLength());

            for (int i = 0; i < itemList.getLength(); i++) {
                Node item = itemList.item(i);
                if (item.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) item;

                    RdaVarietyResponse varietyResponse = RdaVarietyResponse.builder()
                                                                 .cropName(getTagValue("cropNm", element))
                                                                 .varietyName(getTagValue("varietyNm", element))
                                                                 .mainCharInfo(getTagValue("mainChartrInfo", element))
                                                                 .build();

                    result.add(varietyResponse);
                }
            }
        } catch (Exception e) {
            log.error("Error parsing XML response", e);
            e.printStackTrace();
        }

        log.info("Returning {} variety responses", result.size());
        return result;
    }

    // XML 요소에서 특정 태그의 값을 추출
    private String getTagValue(String tag, Element element) {
        try {
            NodeList nodeList = element.getElementsByTagName(tag);
            if (nodeList != null && nodeList.getLength() > 0) {
                Node node = nodeList.item(0);
                if (node != null && node.getChildNodes() != null && node.getChildNodes().getLength() > 0) {
                    return node.getChildNodes().item(0).getNodeValue();
                }
            }
            return "";
        } catch (Exception e) {
            log.error("Error getting tag value for {}", tag, e);
            return "";
        }
    }

}


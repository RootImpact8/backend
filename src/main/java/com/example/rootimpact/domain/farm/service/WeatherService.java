package com.example.rootimpact.domain.farm.service;





import com.example.rootimpact.domain.farm.dto.WeatherResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {
    private final String API_KEY = "a42b1a756c394b6eb03132303250202"; // API 키
    private final String BASE_URL = "https://www.weatherapi.com/";

    public WeatherResponse getWeather(String city) {
        String url = String.format("%s?q=%s&appid=%s&units=metric", BASE_URL, city, API_KEY);
        RestTemplate restTemplate = new RestTemplate();

        return restTemplate.getForObject(url, WeatherResponse.class);
    }
}
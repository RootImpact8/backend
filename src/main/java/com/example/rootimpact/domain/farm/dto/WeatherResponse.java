package com.example.rootimpact.domain.farm.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WeatherResponse {

    private Location location;      // 현재 위치 정보
    private CurrentWeather current; // 현재 날씨 정보
    private Forecast forecast;      // 5일간의 예보 정보

    @Getter
    @Setter
    public static class Location {
        private String name;    // 도시명 (예: "Seoul")
        private String region;  // 지역명 (예: "Seoul")
        private String country; // 국가명 (예: "South Korea")
        private double lat;     // 위도
        private double lon;     // 경도
        private String tz_id;   // 시간대 (예: "Asia/Seoul")
        private String localtime; // 현지 시간
    }

    @Getter
    @Setter
    public static class CurrentWeather {
        private double temp_c;      // 현재 기온 (°C)
        private double temp_f;      // 현재 기온 (°F)
        private boolean is_day;     // 낮/밤 여부 (1: 낮, 0: 밤)
        private double wind_kph;    // 풍속 (km/h)
        private double wind_mph;    // 풍속 (mph)
        private double wind_degree; // 풍향 (각도)
        private String wind_dir;    // 풍향 (예: "NW", "SE")
        private int humidity;       // 습도 (%)
        private double feelslike_c; // 체감 온도 (°C)
        private double feelslike_f; // 체감 온도 (°F)
        private int cloud;          // 구름량 (%)
        private double uv;          // UV 지수
        private Condition condition;
    }

    @Getter
    @Setter
    public static class Condition {
        private String text;  // 날씨 설명 (예: "맑음", "비")
        private String icon;  // 날씨 아이콘 URL
        private int code;     // 날씨 코드
    }

    @Getter
    @Setter
    public static class Forecast {
        private List<ForecastDay> forecastday; // 5일간의 예보 리스트
    }

    @Getter
    @Setter
    public static class ForecastDay {
        private String date; // 날짜 (예: "2025-02-06")
        private Day day;

    }

    @Getter
    @Setter
    public static class Day {
        private double maxtemp_c; // 최고 기온 (°C)
        private double maxtemp_f; // 최고 기온 (°F)
        private double mintemp_c; // 최저 기온 (°C)
        private double mintemp_f; // 최저 기온 (°F)
        private double avgtemp_c; // 평균 기온 (°C)
        private double avgtemp_f; // 평균 기온 (°F)
        private double maxwind_kph; // 최대 풍속 (km/h)
        private double totalprecip_mm; // 강수량 (mm)
        private double totalprecip_in; // 강수량 (in)
        private double avghumidity; // 평균 습도 (%)
        private Condition condition; // 날씨 상태
    }


}
package com.example.rootimpact.domain.farm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "날씨 정보 응답 DTO")
public class WeatherResponse {

    @Schema(description = "현재 위치 정보")
    private Location location;

    @Schema(description = "현재 날씨 정보")
    private CurrentWeather current;

    @Schema(description = "5일간의 예보 정보")
    private Forecast forecast;

    @Getter
    @Setter
    @Schema(description = "위치 정보 DTO")
    public static class Location {
        @Schema(description = "도시명", example = "Seoul")
        private String name;
        @Schema(description = "지역명", example = "Seoul")
        private String region;
        @Schema(description = "국가명", example = "South Korea")
        private String country;
        @Schema(description = "위도", example = "37.5665")
        private double lat;
        @Schema(description = "경도", example = "126.9780")
        private double lon;
        @Schema(description = "시간대", example = "Asia/Seoul")
        private String tz_id;
        @Schema(description = "현지 시간", example = "2025-02-06 12:34")
        private String localtime;
    }

    @Getter
    @Setter
    @Schema(description = "현재 날씨 정보 DTO")
    public static class CurrentWeather {
        @Schema(description = "현재 기온 (°C)", example = "15.5")
        private double temp_c;
        @Schema(description = "현재 기온 (°F)", example = "59.9")
        private double temp_f;
        @Schema(description = "낮/밤 여부 (1: 낮, 0: 밤)", example = "1")
        private boolean is_day;
        @Schema(description = "풍속 (km/h)", example = "10.5")
        private double wind_kph;
        @Schema(description = "풍속 (mph)", example = "6.5")
        private double wind_mph;
        @Schema(description = "풍향 (각도)", example = "180")
        private double wind_degree;
        @Schema(description = "풍향 (예: NW, SE)", example = "SE")
        private String wind_dir;
        @Schema(description = "습도 (%)", example = "70")
        private int humidity;
        @Schema(description = "체감 온도 (°C)", example = "14.0")
        private double feelslike_c;
        @Schema(description = "체감 온도 (°F)", example = "57.2")
        private double feelslike_f;
        @Schema(description = "구름량 (%)", example = "20")
        private int cloud;
        @Schema(description = "UV 지수", example = "5.0")
        private double uv;
        @Schema(description = "강수량 (mm)", example = "0.0")
        private double totalprecip_mm;
        @Schema(description = "날씨 상태")
        private Condition condition;
    }

    @Getter
    @Setter
    @Schema(description = "날씨 상태 DTO")
    public static class Condition {
        @Schema(description = "날씨 설명", example = "맑음")
        private String text;
        @Schema(description = "날씨 아이콘 URL", example = "http://example.com/icon.png")
        private String icon;
        @Schema(description = "날씨 코드", example = "1000")
        private int code;
    }

    @Getter
    @Setter
    @Schema(description = "5일간의 예보 정보 DTO")
    public static class Forecast {
        @Schema(description = "예보 일자 리스트")
        private List<ForecastDay> forecastday;
    }

    @Getter
    @Setter
    @Schema(description = "예보 일자 DTO")
    public static class ForecastDay {
        @Schema(description = "날짜", example = "2025-02-06")
        private String date;
        @Schema(description = "일일 날씨 정보")
        private Day day;
    }

    @Getter
    @Setter
    @Schema(description = "일일 날씨 정보 DTO")
    public static class Day {
        @Schema(description = "최고 기온 (°C)", example = "18.0")
        private double maxtemp_c;
        @Schema(description = "최고 기온 (°F)", example = "64.4")
        private double maxtemp_f;
        @Schema(description = "최저 기온 (°C)", example = "10.0")
        private double mintemp_c;
        @Schema(description = "최저 기온 (°F)", example = "50.0")
        private double mintemp_f;
        @Schema(description = "평균 기온 (°C)", example = "14.0")
        private double avgtemp_c;
        @Schema(description = "평균 기온 (°F)", example = "57.2")
        private double avgtemp_f;
        @Schema(description = "최대 풍속 (km/h)", example = "15.0")
        private double maxwind_kph;
        @Schema(description = "강수량 (mm)", example = "0.0")
        private double totalprecip_mm;
        @Schema(description = "강수량 (in)", example = "0.0")
        private double totalprecip_in;
        @Schema(description = "평균 습도 (%)", example = "60.0")
        private double avghumidity;
        @Schema(description = "날씨 상태")
        private Condition condition;
    }
}
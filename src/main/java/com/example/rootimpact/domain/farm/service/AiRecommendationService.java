package com.example.rootimpact.domain.farm.service;

import com.example.rootimpact.domain.farm.dto.AiRecommendationResponse;
import com.example.rootimpact.domain.farm.dto.WeatherResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.OpenAiChatModel;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiRecommendationService {
    private final OpenAiChatModel openAiChatModel;
    private final WeatherService weatherService;

    public AiRecommendationResponse getRecommendation(String city) {
        // 날씨 데이터 가져오기
        WeatherResponse weather = weatherService.getWeather(city);

        // AI 프롬프트 생성
        PromptTemplate template = new PromptTemplate(
                """
                당신은 농업 전문가입니다.
                아래 데이터를 바탕으로 추천 활동을 작성해 주세요.

                위치: {city}
                날씨: {weatherDescription}
                기온: {temperature}°C
                강수 확률: {rainProbability}

                오늘 사용자가 해야 할 추천 활동은 무엇인가요?
                """
        );

        Prompt prompt = template.create(Map.of(
                "city", city,
                "weatherDescription", weather.getDescription(),
                "temperature", weather.getTemperature(),
                "rainProbability", weather.getHumidity()
        ));

        String aiResponse = openAiChatModel.call(prompt).getResult().getOutput().getContent();

        return AiRecommendationResponse.builder()
                .answer(aiResponse)
                .build();
    }
}

package com.example.rootimpact.domain.farm.service;



import com.example.rootimpact.domain.farm.dto.AiRecommendationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAiService {

    private final OpenAiChatModel openAiChatModel;

    public AiRecommendationResponse getRecommendation(String promptTemplateText, Map<String, Object> variables) {
        // ✅ 프롬프트 템플릿 생성
        PromptTemplate promptTemplate = new PromptTemplate(promptTemplateText);

        // ✅ 템플릿에 변수 적용
        Prompt prompt = promptTemplate.create(variables);

        // ✅ OpenAI API 호출
        String aiResponse = openAiChatModel.call(prompt).getResult().getOutput().getContent();

        log.info("AI 프롬프트 요청: {}", variables);
        log.info("AI 응답: {}", aiResponse);

        // ✅ 응답 반환
        return AiRecommendationResponse.builder()
                .answer(aiResponse)
                .build();
    }
}

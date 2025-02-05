package com.example.rootimpact.domain.farm.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AiNewsResponse {
    private String news; // AI가 반환한 뉴스 내용
}

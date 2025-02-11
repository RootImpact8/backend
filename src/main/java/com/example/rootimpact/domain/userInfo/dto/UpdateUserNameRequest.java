package com.example.rootimpact.domain.userInfo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "사용자 이름 업데이트 요청 DTO")
public class UpdateUserNameRequest {
    @Schema(description = "사용자 이름", example = "홍길동", required = true)
    private String name;
}
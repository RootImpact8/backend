package com.example.rootimpact.domain.userInfo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "사용자 이름 응답 DTO")
public class UserInfoResponse {

    @Schema(description = "사용자 이름", example = "홍길동")
    private String name;
}

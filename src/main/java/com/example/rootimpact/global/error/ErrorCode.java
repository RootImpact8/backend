package com.example.rootimpact.global.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    NOT_FOUND_USER_CROP(HttpStatus.NOT_FOUND, "해당 작물을 찾을 수 없습니다."),
    NOT_FOUND_TASK_TYPE(HttpStatus.NOT_FOUND, "해당 작업을 찾을 수 없습니다."),
    NOT_FOUND_FIRST_DIARY(HttpStatus.NOT_FOUND, "첫 번째 일기를 찾을 수 없습니다."),
    NOT_FOUND_SOWLING_TASK(HttpStatus.NOT_FOUND, "해당 작물의 파종 기준 작업이 정의되지 않았습니다: %s"),
    NOT_FOUND_CROP_DIARY(HttpStatus.NOT_FOUND, "해당 작물에 대한 작성된 일기가 없습니다."),
    NOT_FOUND_USER_LOCATION(HttpStatus.NOT_FOUND, "사용자 위치 정보를 찾을 수 없습니다."),
    NOT_FOUND_USER_INFO(HttpStatus.NOT_FOUND, "사용자 정보를 찾을 수 없습니다."),
    NOT_FOUND_CULTIVATED_CROP(HttpStatus.NOT_FOUND, "재배 작물 정보를 찾을 수 없습니다."),
    NOT_FOUND_INTEREST_CROP(HttpStatus.NOT_FOUND, "관심 작물 정보를 찾을 수 없습니다."),
    NOT_EXIST_USER_PASSWORD(HttpStatus.NOT_FOUND, "비밀번호가 일치하지 않습니다."),

    ALREADY_REGISTERED_USER_EMAIL(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일 입니다."),

    REQUIRED_USER_ID(HttpStatus.BAD_REQUEST, "사용자 ID는 필수입니다."),
    REQUIRED_TASK_ID(HttpStatus.BAD_REQUEST, "작업 ID는 필수입니다."),
    REQUIRED_CROP_NAME(HttpStatus.BAD_REQUEST, "작물 이름은 필수입니다."),

    FAILED_UPLOAD_IMG(HttpStatus.BAD_REQUEST, "이미지 업로드에 실패하였습니다."),
    FAILED_FETCH_COORDINATES(HttpStatus.INTERNAL_SERVER_ERROR, "주소에 대한 좌표를 가져오는데 실패했습니다."),
    FAILED_PRICE_API(HttpStatus.INTERNAL_SERVER_ERROR, "가격 조회 API 호출 실패: %s"),
    FAILED_RESPONSE_PROCESSING(HttpStatus.INTERNAL_SERVER_ERROR, "응답 처리 실패: %s"),
    FAILED_FETCH_WEATHER(HttpStatus.INTERNAL_SERVER_ERROR, "날씨 정보를 가져오는데 실패했습니다: %s"),

    INVALID_CROP_NAME(HttpStatus.BAD_REQUEST, "유효하지 않은 작물 이름입니다: %s"),

    NO_PRICE_DATA(HttpStatus.OK, "해당 작물의 가격 정보가 없습니다."),
    NO_INTEREST_CROP(HttpStatus.FORBIDDEN, "해당 작물은 관심 작물에 등록되어 있지 않습니다."),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}

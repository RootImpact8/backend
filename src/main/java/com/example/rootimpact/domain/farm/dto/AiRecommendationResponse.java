package com.example.rootimpact.domain.farm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Schema(description = "AI가 날씨, 거주지, 작물, 일기에 응답 DTO")
public class AiRecommendationResponse {

    @Schema(description = "작물 재배 일차", example = "7일차")
    private String cropStage;  // ✅ 재배 일차 정보

    @Schema(description = "오늘의 이상기후 여부", example = "true")
    private boolean isExtremeWeather; // ✅ 이상기후 발생 여부 (true = 이상기후 있음)

    // ✅ 이상기후가 발생했을 때만 응답됨
    @Schema(description = "이상기후 요약", example = "오늘 한파 경보! 주의하세요.")
    private String climateWarning;

    @Schema(description = "이상기후 대응 방법", example = "감자를 비닐 덮개로 보호하고, 지온을 유지하기 위해 멀칭을 강화하세요.")
    private String climateAdvice;

    // ✅ 이상기후가 없을 때만 응답됨
    @Schema(description = "오늘의 재배 활동 요약", example = "오늘 감자에 물을 주는 것이 중요합니다.")
    private String summary;

    @Schema(description = "AI가 제공하는 상세한 재배 조언", example = "감자는 현재 7일차 재배 중이며, 기온이 20도이므로 오전에 물을 주는 것이 효과적입니다. 또한, 오늘 습도가 50%로 다소 건조하므로 관수량을 늘리는 것이 좋습니다. 비료는 3일 후 추가하는 것이 적절합니다.")
    private String detailedAdvice;
}
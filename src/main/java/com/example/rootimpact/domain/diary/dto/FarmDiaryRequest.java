package com.example.rootimpact.domain.diary.dto;

import com.example.rootimpact.domain.diary.entity.TempImageInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "일기 생성 및 수정 요청 DTO")
public class FarmDiaryRequest {

    @NotNull(message = "사용자 ID는 필수입니다.")
    @Schema(description = "사용자 ID", example = "1", required = true)
    private Long userId;

    @NotNull(message = "작성 날짜는 필수입니다.")
    @Schema(description = "작성 날짜 (yyyy-MM-dd)", example = "2023-09-15", required = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate writeDate;

    @NotBlank(message = "작물명은 필수입니다.")
    @Schema(description = "사용자 작물명", example = "감자", required = true)
    private String userCropName;

    @NotNull(message = "작업 ID는 필수입니다.")
    @Schema(description = "작업 ID", example = "2", required = true)
    private Long taskId;
    
    @Schema(description = "일기 내용", example = "Planted new seeds.")
    private String content;

    @Schema(description = "저장된 이미지 정보 목록")
    private List<TempImageInfo> savedImages;

    @Schema(description = "삭제할 이미지 ID 목록")
    private List<Long> deleteImageIds;

    // getter 메서드들
    public List<TempImageInfo> getSavedImages() {
        return savedImages != null ? savedImages : new ArrayList<>();
    }

    public List<Long> getDeleteImageIds() {
        return deleteImageIds != null ? deleteImageIds : new ArrayList<>();
    }
}

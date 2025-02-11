package com.example.rootimpact.domain.diary.controller;

import com.example.rootimpact.domain.diary.dto.FarmDiaryRequest;
import com.example.rootimpact.domain.diary.dto.FarmDiaryResponse;
import com.example.rootimpact.domain.diary.dto.TaskReponseDto;
import com.example.rootimpact.domain.diary.dto.UserCropResponseDto;
import com.example.rootimpact.domain.diary.service.FarmDiaryService;
import com.example.rootimpact.domain.user.entity.User;
import com.example.rootimpact.domain.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/diary")
@RequiredArgsConstructor
@Tag(name = "Diary", description = "일기 관련 API")
public class FarmDiaryController {

    private final FarmDiaryService farmDiaryService;
    private final UserRepository userRepository;

    @Operation(summary = "작업 유형 조회", description = "특정 작물에 대한 작업 유형 목록을 조회합니다.")
    @GetMapping("/tasks/{cropName}")
    public ResponseEntity<List<TaskReponseDto>> getTasksByCrop(
            @Parameter(description = "조회할 작물명", required = true)
            @PathVariable("cropName") String cropName) {
        return ResponseEntity.ok(farmDiaryService.getTaskTypes(cropName));
    }

    @Operation(summary = "사용자별 재배 작물 조회", description = "현재 인증된 사용자의 재배 작물 목록을 조회합니다.")
    @GetMapping("/user-crops")
    public ResponseEntity<List<UserCropResponseDto>> getUserCrops(Authentication authentication) {
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(farmDiaryService.getUserCrops(user.getId()));
    }

    @Operation(summary = "일기 생성", description = "새로운 일기를 생성합니다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FarmDiaryResponse> createFarmDiary(
            @Valid @ModelAttribute
            @Parameter(description = "일기 생성 요청 객체", required = true)
            FarmDiaryRequest request) {
        log.debug("Received request: {}", request); // 로깅 추가
        FarmDiaryResponse response = farmDiaryService.create(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "일기 수정", description = "기존 일기를 수정합니다.")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FarmDiaryResponse> updateFarmDiary(
            @Parameter(description = "수정할 일기의 ID", required = true)
            @PathVariable("id") Long id,
            @Valid @ModelAttribute
            @Parameter(description = "수정할 일기 정보", required = true)
            FarmDiaryRequest request) {
        FarmDiaryResponse response = farmDiaryService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "일기 조회", description = "ID에 해당하는 일기를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<FarmDiaryResponse> findById(
            @Parameter(description = "조회할 일기의 ID", required = true)
            @PathVariable("id") Long id) {
        FarmDiaryResponse response = farmDiaryService.findById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "사용자별 일기 조회", description = "특정 사용자의 일기 목록을 조회합니다.")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FarmDiaryResponse>> findByUserId(
            @Parameter(description = "사용자의 ID", required = true)
            @PathVariable("userId") Long userId) {
        return ResponseEntity.ok(farmDiaryService.findByUserId(userId));
    }

    @Operation(summary = "날짜별 일기 조회", description = "특정 날짜에 작성된 일기 목록을 조회합니다.")
    @GetMapping("/date/{writeDate}")
    public ResponseEntity<List<FarmDiaryResponse>> findByWriteDate(
            @Parameter(description = "작성 날짜 (yyyy-MM-dd)", required = true)
            @PathVariable("writeDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate writeDate) {
        List<FarmDiaryResponse> response = farmDiaryService.findByWriteDate(writeDate);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "작물별 일기 조회", description = "특정 작물의 일기 목록을 조회합니다.")
    @GetMapping("/crops/{cropName}")
    public ResponseEntity<List<FarmDiaryResponse>> findByCropName(
            @Parameter(description = "조회할 작물명", required = true)
            @PathVariable("cropName") String cropName) {
        return ResponseEntity.ok(farmDiaryService.findByCropName(cropName));
    }

    @Operation(summary = "일기 삭제", description = "ID에 해당하는 일기를 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFarmDiary(
            @Parameter(description = "삭제할 일기의 ID", required = true)
            @PathVariable("id") Long id) {
        farmDiaryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "첫 번째 일기 작성 날짜(파종일) 조회", description = "특정 작물의 첫 번째 일기 작성 날짜를 조회합니다.")
    @GetMapping("/first-diary-date")
    public ResponseEntity<LocalDate> getFirstDiaryDate(
            @Parameter(description = "사용자 ID", required = true)
            @RequestParam Long userId,
            @Parameter(description = "작물명", required = true)
            @RequestParam String cropName) {
        LocalDate firstDiaryDate = farmDiaryService.getFirstDiaryDate(userId, cropName);
        return ResponseEntity.ok(firstDiaryDate);
    }

    @Operation(summary = "예상 수확일 조회", description = "특정 작물의 예상 수확일을 조회합니다.")
    @GetMapping("/ai-harvest-estimate")
    public ResponseEntity<String> getHarvestEstimate(
            @Parameter(description = "사용자 ID", required = true)
            @RequestParam Long userId,
            @Parameter(description = "작물명", required = true)
            @RequestParam String cropName) {
        String harvestEstimate = farmDiaryService.getPredictedHarvestDate(userId, cropName);
        return ResponseEntity.ok(harvestEstimate);
    }

    @Operation(summary = "마지막 일기 조회", description = "인증 없이 특정 사용자의 특정 작물의 가장 마지막 일기를 조회합니다.")
    @GetMapping("/last-diary")
    public ResponseEntity<FarmDiaryResponse> getLastDiaryEntry(
            @Parameter(description = "사용자 ID", required = true)
            @RequestParam Long userId,
            @Parameter(description = "작물명", required = true)
            @RequestParam String cropName) {
        FarmDiaryResponse lastDiary = farmDiaryService.getLastDiaryEntry(userId, cropName);
        return ResponseEntity.ok(lastDiary);
    }
}
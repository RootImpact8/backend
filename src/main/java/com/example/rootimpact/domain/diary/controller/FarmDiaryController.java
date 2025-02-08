package com.example.rootimpact.domain.diary.controller;

import com.example.rootimpact.domain.diary.dto.FarmDiaryRequest;
import com.example.rootimpact.domain.diary.dto.FarmDiaryResponse;
import com.example.rootimpact.domain.diary.dto.TaskReponseDto;
import com.example.rootimpact.domain.diary.dto.UserCropResponseDto;
import com.example.rootimpact.domain.diary.service.FarmDiaryService;
import com.example.rootimpact.domain.user.entity.User;
import com.example.rootimpact.domain.user.repository.UserRepository;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/diary")
@RequiredArgsConstructor
public class FarmDiaryController {

    private final FarmDiaryService farmDiaryService;
    private final UserRepository userRepository;

    // 작업 유형 조회
    @GetMapping("/tasks/{cropName}")
    public ResponseEntity<List<TaskReponseDto>> getTasksByCrop(@PathVariable("cropName") String cropName) {
        return ResponseEntity.ok(farmDiaryService.getTaskTypes(cropName));
    }

    // 사용자별 재배 작물 조회
    @GetMapping("/user-crops")
    public ResponseEntity<List<UserCropResponseDto>> getUserCrops(Authentication authentication) {
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail)
                            .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(farmDiaryService.getUserCrops(user.getId()));
    }

    // 일기 생성
    @PostMapping
    public ResponseEntity<FarmDiaryResponse> createFarmDiary(
            @Valid @RequestBody FarmDiaryRequest request) {
        FarmDiaryResponse response = farmDiaryService.create(request);
        return ResponseEntity.ok(response);
    }

    // 일기 수정
    @PutMapping("/{id}")
    public ResponseEntity<FarmDiaryResponse> updateFarmDiary(
            @PathVariable("id") Long id,
            @Valid @RequestBody FarmDiaryRequest request) {
        FarmDiaryResponse response = farmDiaryService.update(id, request);
        return ResponseEntity.ok(response);
    }

    // 일기 조회
    @GetMapping("/{id}")
    public ResponseEntity<FarmDiaryResponse> findById(
            @PathVariable("id") Long id) {
        FarmDiaryResponse response = farmDiaryService.findById(id);
        return ResponseEntity.ok(response);
    }

    // 사용자별 일기 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FarmDiaryResponse>> findByUserId(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(farmDiaryService.findByUserId(userId));
    }

    // 날짜별 일기 조회
    @GetMapping("/date/{writeDate}")
    public ResponseEntity<List<FarmDiaryResponse>> findByWriteDate(
            @PathVariable("writeDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate writeDate) {
        List<FarmDiaryResponse> response = farmDiaryService.findByWriteDate(writeDate);
        return ResponseEntity.ok(response);
    }

    // 작물별 일기 조회
    @GetMapping("/crops/{cropName}")
    public ResponseEntity<List<FarmDiaryResponse>> findByCropName(@PathVariable("cropName") String cropName) {
        return ResponseEntity.ok(farmDiaryService.findByCropName(cropName));
    }

    // 일기 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFarmDiary(
            @PathVariable("id") Long id) {
        farmDiaryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ 특정 작물의 첫 번째 일기 작성 날짜(파종일) 조회
    @GetMapping("/first-diary-date")
    public ResponseEntity<LocalDate> getFirstDiaryDate(
            @RequestParam Long userId,
            @RequestParam String cropName) {

        LocalDate firstDiaryDate = farmDiaryService.getFirstDiaryDate(userId, cropName);
        return ResponseEntity.ok(firstDiaryDate);
    }

    // ✅ 특정 작물의 예상 수확일 조회
    @GetMapping("/ai-harvest-estimate")
    public ResponseEntity<String> getHarvestEstimate(
            @RequestParam Long userId,
            @RequestParam String cropName) {

        String harvestEstimate = farmDiaryService.getPredictedHarvestDate(userId, cropName);
        return ResponseEntity.ok(harvestEstimate);
    }
}

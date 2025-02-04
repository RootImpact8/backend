package com.example.rootimpact.domain.diary.controller;

import com.example.rootimpact.domain.diary.dto.FarmDiaryRequest;
import com.example.rootimpact.domain.diary.dto.FarmDiaryResponse;
import com.example.rootimpact.domain.diary.entity.FarmDiary;
import com.example.rootimpact.domain.diary.service.FarmDiaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/diary")
@RequiredArgsConstructor
@Tag(name = "Farm Diary", description = "농사 일지 API")
public class FarmDiaryController {

    private final FarmDiaryService farmDiaryService;

    @Operation(summary = "일기 생성", description = "새로운 농사 일지를 생성합니다.")
    @PostMapping
    public ResponseEntity<FarmDiaryResponse> createFarmDiary(
            @Valid @RequestBody FarmDiaryRequest request) {
        FarmDiaryResponse response = farmDiaryService.create(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "일기 수정", description = "기존 농사 일지를 수정합니다.")
    @PutMapping("/{id}")
    public ResponseEntity<FarmDiaryResponse> updateFarmDiary(
            @PathVariable("id") Long id,
            @Valid @RequestBody FarmDiaryRequest request) {
        FarmDiaryResponse response = farmDiaryService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "일기 조회", description = "ID로 농사 일지를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<FarmDiaryResponse> findById(
            @PathVariable("id") Long id) {
        FarmDiaryResponse response = farmDiaryService.findById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "사용자 일기 목록 조회", description = "특정 사용자의 모든 농사 일지를 조회합니다.")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FarmDiaryResponse>> findByUserId(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(farmDiaryService.findByUserId(userId));
    }

    @Operation(summary = "날짜별 일기 목록 조회", description = "특정 날짜의 농사 일지를 조회합니다.")
    @GetMapping("/date/{writeDate}")
    public ResponseEntity<List<FarmDiaryResponse>> findByWriteDate(
            @PathVariable("writeDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate writeDate) {
        List<FarmDiaryResponse> response = farmDiaryService.findByWriteDate(writeDate);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "일기 삭제", description = "농사 일지를 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFarmDiary(
            @PathVariable("id") Long id) {
        farmDiaryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

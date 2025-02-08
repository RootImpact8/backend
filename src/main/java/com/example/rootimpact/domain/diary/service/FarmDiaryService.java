package com.example.rootimpact.domain.diary.service;

import com.example.rootimpact.domain.diary.dto.FarmDiaryRequest;
import com.example.rootimpact.domain.diary.dto.FarmDiaryResponse;
import com.example.rootimpact.domain.diary.dto.TaskReponseDto;
import com.example.rootimpact.domain.diary.dto.UserCropResponseDto;
import com.example.rootimpact.domain.diary.entity.FarmDiary;
import com.example.rootimpact.domain.diary.entity.Task;
import com.example.rootimpact.domain.diary.repository.FarmDiaryRepository;
import com.example.rootimpact.domain.diary.repository.TaskRepository;
import com.example.rootimpact.domain.user.entity.User;
import com.example.rootimpact.domain.user.repository.UserRepository;
import com.example.rootimpact.domain.userInfo.entity.UserCrop;
import com.example.rootimpact.domain.userInfo.repository.UserCropRepository;
import com.example.rootimpact.domain.userInfo.service.UserInfoService;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FarmDiaryService {

    private final FarmDiaryRepository farmDiaryRepository;
    private final UserRepository userRepository;
    private final UserCropRepository userCropRepository;
    private final TaskRepository taskRepository;
    private final UserInfoService userInfoService;

    // 사용자 재배 작물 조회
    public List<UserCropResponseDto> getUserCrops(Long userId) {
        List<UserCrop> cultivatedCrops = userInfoService.getCultivatedCrops(userId);

        return cultivatedCrops.stream()
                       .map(UserCropResponseDto::new)
                       .collect(Collectors.toList());
    }

    // 선택된 작물의 작업 목록 조회
    public List<TaskReponseDto> getTaskTypes(String cropName) {
        return taskRepository.findByCropName(cropName)
                       .stream()
                       .map(TaskReponseDto::new)
                       .collect(Collectors.toList());
    }

    // 일기 생성 메서드
    @Transactional
    public FarmDiaryResponse create(FarmDiaryRequest request) {
        // 사용자 조회
        User user = userRepository.findById(request.getUserId()).orElseThrow(()->new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // UserCrop 조회
        UserCrop userCrop = userInfoService.getCultivatedCrops(request.getUserId()).stream()
                                    .filter(crop -> crop.getCropName().equals(request.getUserCropName()))
                                    .findFirst()
                                    .orElseThrow(() -> new IllegalArgumentException(
                                            "해당 작물을 찾을 수 없습니다."));

        // TaskType 조회
        Task task = taskRepository.findById(request.getTaskId())
                                    .orElseThrow(() -> new IllegalArgumentException("해당 작업을 찾을 수 없습니다."));

        // 영농일지 생성
        FarmDiary farmDiary =  FarmDiary.builder()
                                       .user(user)
                                       .writeDate(request.getWriteDate())
                                       .userCrop(userCrop)
                                       .task(task)
                                       .content(request.getContent())
                                       .build();

        // 저장 후 응답 DTO 변환
        FarmDiary saved = farmDiaryRepository.save(farmDiary);
        return new FarmDiaryResponse(saved);
    }

    // 일기 수정 메서드
    @Transactional
    public FarmDiaryResponse update(Long diaryId, FarmDiaryRequest request) {
        // 영농일지 조회
        FarmDiary farmDiary = farmDiaryRepository.findById(diaryId).orElseThrow(()->new IllegalArgumentException("일기를 찾을 수 없습니다."));

        // 작성자 본인 확인
        if (!farmDiary.getUser().getId().equals(request.getUserId())) {
            throw new IllegalArgumentException("영농일지 수정 권한이 없습니다.");
        }
;
        // UserCrop 조회
        UserCrop userCrop = userCropRepository.findCultivatedCropsByUser(farmDiary.getUser()).stream()
                                    .filter(crop -> crop.getCropName().equals(request.getUserCropName()))
                                    .findFirst()
                                    .orElseThrow(() -> new IllegalArgumentException("해당 작물을 찾을 수 없습니다."));

        // Task 조회
        Task task = taskRepository.findById(request.getTaskId())
                                    .orElseThrow(() -> new IllegalArgumentException("해당 작업을 찾을 수 없습니다."));

        // 수정
        farmDiary.update(
                request.getWriteDate(),
                userCrop,
                task,
                request.getContent()
        );

        return new FarmDiaryResponse(farmDiary);
    }

    // 일기 ID로 일기 조회
    public FarmDiaryResponse findById(Long id) {
        FarmDiary farmDiary = farmDiaryRepository.findById(id).orElseThrow(()->new IllegalArgumentException("일기를 찾을 수 없습니다."));
        return new FarmDiaryResponse(farmDiary);
    }

    // 사용자별 일기 조회
    public List<FarmDiaryResponse> findByUserId(Long userId) {
        List<FarmDiary> diaries = farmDiaryRepository.findByUserId(userId);
        return diaries.stream().map(FarmDiaryResponse::new)
                       .collect(Collectors.toList());
    }

    // 작성일별 일기 조회
    public List<FarmDiaryResponse> findByWriteDate(LocalDate writeDate) {
        List<FarmDiary> diaries = farmDiaryRepository.findByWriteDate(writeDate);
        return diaries.stream().map(FarmDiaryResponse::new)
                       .collect(Collectors.toList());
    }

    // 작물별 일기 조회
    public List<FarmDiaryResponse> findByCropName(String cropName) {
        List<FarmDiary> diaries = farmDiaryRepository.findByUserCrop_CropName(cropName);
        return diaries.stream().map(FarmDiaryResponse::new)
                       .collect(Collectors.toList());
    }

    // 일기 삭제
    @Transactional
    public void delete(Long id) {
        FarmDiary farmDiary = farmDiaryRepository.findById(id).orElseThrow(()->new IllegalArgumentException("일기를 찾을 수 없습니다."));
        farmDiaryRepository.delete(farmDiary);
    }
}

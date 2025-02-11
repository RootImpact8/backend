package com.example.rootimpact.domain.diary.service;

import com.example.rootimpact.domain.diary.dto.FarmDiaryRequest;
import com.example.rootimpact.domain.diary.dto.FarmDiaryResponse;
import com.example.rootimpact.domain.diary.dto.TaskReponseDto;
import com.example.rootimpact.domain.diary.dto.UserCropResponseDto;
import com.example.rootimpact.domain.diary.entity.DiaryImage;
import com.example.rootimpact.domain.diary.entity.FarmDiary;
import com.example.rootimpact.domain.diary.entity.Task;
import com.example.rootimpact.domain.diary.repository.FarmDiaryRepository;
import com.example.rootimpact.domain.diary.repository.TaskRepository;
import com.example.rootimpact.domain.farm.dto.WeatherResponse;
import com.example.rootimpact.domain.farm.service.OpenAiService;
import com.example.rootimpact.domain.farm.service.WeatherService;
import com.example.rootimpact.domain.user.entity.User;
import com.example.rootimpact.domain.user.repository.UserRepository;
import com.example.rootimpact.domain.userInfo.entity.UserCrop;
import com.example.rootimpact.domain.userInfo.repository.UserCropRepository;
import com.example.rootimpact.domain.userInfo.service.UserInfoService;
import com.example.rootimpact.global.config.FileConfig;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class FarmDiaryService {

    private final FarmDiaryRepository farmDiaryRepository;
    private final UserRepository userRepository;
    private final UserCropRepository userCropRepository;
    private final TaskRepository taskRepository;
    private final UserInfoService userInfoService;
    private final OpenAiService openAiService;
    private final WeatherService weatherService;
    private final FileConfig fileConfig;

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
        log.debug("Creating farm diary with request: {}", request);

        // null 체크 추가
        if (request.getUserId() == null) {
            throw new IllegalArgumentException("사용자 ID는 필수입니다.");
        }
        if (request.getTaskId() == null) {
            throw new IllegalArgumentException("작업 ID는 필수입니다.");
        }
        if (request.getUserCropName() == null || request.getUserCropName().trim().isEmpty()) {
            throw new IllegalArgumentException("작물 이름은 필수입니다.");
        }

        // 사용자 조회
        User user = userRepository.findById(request.getUserId())
                            .orElseThrow(()->new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // UserCrop 조회
        UserCrop userCrop = userInfoService.getCultivatedCrops(request.getUserId()).stream()
                                    .filter(crop -> crop.getCropName().equals(request.getUserCropName()))
                                    .findFirst()
                                    .orElseThrow(() -> new IllegalArgumentException("해당 작물을 찾을 수 없습니다."));

        // TaskType 조회
        Task task = taskRepository.findById(request.getTaskId())
                            .orElseThrow(() -> new IllegalArgumentException("해당 작업을 찾을 수 없습니다."));

        // 영농일지 생성
        FarmDiary farmDiary = FarmDiary.builder()
                                      .user(user)
                                      .writeDate(request.getWriteDate())
                                      .userCrop(userCrop)
                                      .task(task)
                                      .content(request.getContent())
                                      .build();

        // 새 이미지 추가
        if(request.getImages() != null && !request.getImages().isEmpty()) {
            List<DiaryImage> diaryImages = new ArrayList<>();

            for(MultipartFile imageFile : request.getImages()) {
                try {
                    String originalFileName = imageFile.getOriginalFilename();
                    String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
                    String savedFileName = UUID.randomUUID().toString() + fileExtension;

                    // FileConfig를 사용하여 전체 경로 생성
                    String fullPath = fileConfig.getUploadPath() + savedFileName;
                    File dest = new File(fullPath);

                    // 디렉토리가 없으면 생성
                    dest.getParentFile().mkdirs();

                    imageFile.transferTo(dest);

                    DiaryImage diaryImage = DiaryImage.builder()
                                                    .farmDiary(farmDiary)
                                                    .originalFileName(originalFileName)
                                                    .savedFileName(savedFileName)
                                                    .filePath(fullPath)
                                                    .build();

                    diaryImages.add(diaryImage);
                } catch (IOException e) {
                    throw new RuntimeException("이미지 저장에 실패했습니다.", e);
                }
            }

            farmDiary.setImages(diaryImages);
        }

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

        // 기존 이미지 삭제
        if (request.getDeleteImageIds() != null && !request.getDeleteImageIds().isEmpty()) {
            farmDiary.getImages().removeIf(image ->
                                                   request.getDeleteImageIds().contains(image.getId()));
        }

        // 새 이미지 추가
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            for (MultipartFile imageFile : request.getImages()) {
                try {
                    String originalFileName = imageFile.getOriginalFilename();
                    String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
                    String savedFileName = UUID.randomUUID().toString() + fileExtension;

                    // FileConfig를 사용하여 전체 경로 생성
                    String fullPath = fileConfig.getUploadPath() + savedFileName;
                    File dest = new File(fullPath);
                    // 디렉토리가 없으면 생성
                    dest.getParentFile().mkdirs();

                    imageFile.transferTo(dest);

                    // 이미지 엔티티 생성 및 추가
                    DiaryImage diaryImage = DiaryImage.builder()
                                                    .farmDiary(farmDiary)
                                                    .originalFileName(originalFileName)
                                                    .savedFileName(savedFileName)
                                                    .filePath(fullPath)
                                                    .build();

                    farmDiary.addImage(diaryImage);
                } catch (IOException e) {
                    throw new RuntimeException("이미지 저장에 실패했습니다.", e);
                }
            }
        }

        // 영농일지 정보 수정
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
    /**
     * ✅ 1️⃣ 특정 작물의 첫 번째 일기 작성 날짜(파종일) 조회
     */
    public LocalDate getFirstDiaryDate(Long userId, String cropName) {
        return farmDiaryRepository.findTopByUserIdAndUserCrop_CropNameOrderByWriteDateAsc(userId, cropName)
                .map(FarmDiary::getWriteDate)
                .orElseThrow(() -> new RuntimeException("첫 번째 일기를 찾을 수 없습니다."));
    }
    /**
     * ✅ 2️⃣ 특정 작물의 예상 수확일 계산 (AI + 날씨 정보 반영)
     */
    public String getPredictedHarvestDate(Long userId, String cropName) {
        // ✅ 1️⃣ 특정 작물의 첫 일기 작성 날짜(파종일) 가져오기
        LocalDate sowingDate = getFirstDiaryDate(userId, cropName);

        // ✅ 2️⃣ 사용자 위치 기반 날씨 데이터 가져오기
        WeatherResponse weatherResponse = weatherService.getWeatherByUserId(userId);

        // ✅ 3️⃣ AI 프롬프트 생성 및 요청
        String promptTemplate = """
                당신은 농업 전문가입니다.
                주어진 작물 {cropName}의 재배 주기를 고려하여 예상 수확일을 계산하세요.
                현재 위치의 날씨 데이터를 반영하여 기온 및 습도 변동에 따른 영향을 고려하세요.
                
                작물: {cropName}
                파종일: {sowingDate}
                현재 위치: {location}
                현재 날씨: {weather}
                기온: {temperature}°C
                습도: {humidity}%
                
                예상 수확일을 날짜 형식(YYYY-MM-DD)으로 한 줄만 출력하세요.
                """;

        Map<String, Object> variables = Map.of(
                "cropName", cropName,
                "sowingDate", sowingDate.toString(),
                "location", weatherResponse.getLocation().getName(),
                "weather", weatherResponse.getCurrent().getCondition().getText(),
                "temperature", weatherResponse.getCurrent().getTemp_c(),
                "humidity", weatherResponse.getCurrent().getHumidity()
        );

        // ✅ AI 요청 (예상 수확일 반환)
        return openAiService.getRecommendation(promptTemplate, variables);
    }
    // ✅ 특정 작물에 대한 마지막 작성 일기 조회
    @Transactional(readOnly = true)
    public FarmDiaryResponse getLastDiaryEntry(Long userId, String cropName) {
        Optional<FarmDiary> lastDiary = farmDiaryRepository.findTopByUserIdAndUserCrop_CropNameOrderByWriteDateDesc(userId, cropName);

        return lastDiary.map(FarmDiaryResponse::new)
                .orElseThrow(() -> new RuntimeException("해당 작물에 대한 작성된 영농일기가 없습니다."));
    }


}

package com.example.rootimpact.domain.diary.service;

import com.example.rootimpact.domain.diary.dto.*;
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
import com.example.rootimpact.global.error.ErrorCode;
import com.example.rootimpact.global.exception.GlobalException;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
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
    public List<TaskReponseDto> getTaskTypes(Long cropId) {
        return taskRepository.findByCropId(cropId)
                .stream()
                .map(TaskReponseDto::new)
                .collect(Collectors.toList());
    }

    // 여러 이미지 파일 업로드
    public List<TempImageResponse> uploadImages(List<MultipartFile> images) {
        List<TempImageResponse> uploadedImages = new ArrayList<>();

        for (MultipartFile image : images) {
            try {
                TempImageResponse uploadedImage = saveImage(image);
                uploadedImages.add(uploadedImage);
            } catch (IOException e) {
                throw new GlobalException(ErrorCode.FAILED_UPLOAD_IMG);
            }
        }

        return uploadedImages;
    }

    // 하나의 이미지 파일 저장
    private TempImageResponse saveImage(MultipartFile image) throws IOException {
        // 원본 파일명과 확장자 추출
        String originalFileName = image.getOriginalFilename();
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));

        // UUID를 사용하여 고유한 파일명 생성
        String savedFileName = UUID.randomUUID().toString() + fileExtension;

        // FileConfig에 설정된 경로와 파일명을 조합하여 전체 경로 생성
        String fullPath = fileConfig.getUploadPath() + savedFileName;
        File dest = new File(fullPath);

        // 저장 디렉토리가 없는 경우 생성
        dest.getParentFile().mkdirs();

        // MultipartFile을 실제 파일로 저장
        image.transferTo(dest);

        // 저장된 이미지 정보를 담은 응답 객체 반환
        return new TempImageResponse(
                originalFileName,  // 원본 파일명
                savedFileName,    // 저장된 파일명 (UUID)
                fullPath         // 전체 파일 경로
        );
    }

    // 일기 생성 메서드
    @Transactional
    public FarmDiaryResponse create(FarmDiaryRequest request) {
        log.debug("Creating farm diary with request: {}", request);

        // null 체크 추가
        if (request.getUserId() == null) {
            throw new GlobalException(ErrorCode.REQUIRED_USER_ID);
        }
        if (request.getTaskId() == null) {
            throw new GlobalException(ErrorCode.REQUIRED_TASK_ID);
        }
        if (request.getUserCropName() == null || request.getUserCropName().trim().isEmpty()) {
            throw new GlobalException(ErrorCode.REQUIRED_CROP_NAME);
        }

        // 사용자 조회
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(()->new GlobalException(ErrorCode.NOT_FOUND_USER));

        // UserCrop 조회
        UserCrop userCrop = userInfoService.getCultivatedCrops(request.getUserId()).stream()
                .filter(crop -> crop.getCropName().equals(request.getUserCropName()))
                .findFirst()
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_USER_CROP));

        // TaskType 조회
        Task task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_TASK_TYPE));

        // 영농일지 생성
        FarmDiary farmDiary = FarmDiary.builder()
                .user(user)
                .writeDate(request.getWriteDate())
                .userCrop(userCrop)
                .task(task)
                .content(request.getContent())
                .build();

        // 이미지 정보 추가
        if(request.getSavedImages() != null && !request.getSavedImages().isEmpty()) {
            List<DiaryImage> diaryImages = request.getSavedImages().stream()
                    .map(imageInfo -> DiaryImage.builder()
                            .farmDiary(farmDiary)
                            .originalFileName(imageInfo.getOriginalFileName())
                            .savedFileName(imageInfo.getSavedFileName())
                            .filePath(imageInfo.getFilePath())
                            .build())
                    .collect(Collectors.toList());

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
            throw new GlobalException(ErrorCode.NOT_FOUND_USER);
        }
        ;
        // UserCrop 조회
        UserCrop userCrop = userCropRepository.findCultivatedCropsByUser(farmDiary.getUser()).stream()
                .filter(crop -> crop.getCropName().equals(request.getUserCropName()))
                .findFirst()
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_USER_CROP));

        // Task 조회
        Task task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_TASK_TYPE));

        // 기존 이미지 삭제
        if (request.getDeleteImageIds() != null && !request.getDeleteImageIds().isEmpty()) {
            farmDiary.getImages().removeIf(image ->
                    request.getDeleteImageIds().contains(image.getId()));
        }

        // 새 이미지 추가
        if (request.getSavedImages() != null && !request.getSavedImages().isEmpty()) {
            List<DiaryImage> newImages = request.getSavedImages().stream()
                    .map(imageInfo -> DiaryImage.builder()
                            .farmDiary(farmDiary)
                            .originalFileName(imageInfo.getOriginalFileName())
                            .savedFileName(imageInfo.getSavedFileName())
                            .filePath(imageInfo.getFilePath())
                            .build())
                    .collect(Collectors.toList());

            newImages.forEach(farmDiary::addImage);
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
    public List<FarmDiaryResponse> findByCropId(Long cropId) {
        List<FarmDiary> diaries = farmDiaryRepository.findByUserCrop_CropId(cropId);
        return diaries.stream()
                .map(FarmDiaryResponse::new)
                .collect(Collectors.toList());
    }

    // 일기 삭제
    @Transactional
    public void delete(Long id) {
        FarmDiary farmDiary = farmDiaryRepository.findById(id).orElseThrow(()->new IllegalArgumentException("일기를 찾을 수 없습니다."));
        farmDiaryRepository.delete(farmDiary);
    }

    // 특정 작물의 첫 번째 일기 작성 날짜(파종일) 조회
    public LocalDate getFirstDiaryDate(Long userId, Long cropId) {
        return farmDiaryRepository.findTopByUserIdAndUserCrop_CropIdOrderByWriteDateAsc(userId, cropId)
                .map(FarmDiary::getWriteDate)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_FIRST_DIARY));
    }
    // SOWING_TASK_IDS: 작물별 파종/묘목 관련 작업 ID 매핑 (숫자로 정의)
    private static final Map<Long, List<Long>> SOWING_TASK_IDS = new HashMap<>();

    static {
        SOWING_TASK_IDS.put(3L, List.of(67L));       // 감자
        SOWING_TASK_IDS.put(1L, List.of(5L));        // 딸기
        SOWING_TASK_IDS.put(4L, List.of(93L));       // 상추
        SOWING_TASK_IDS.put(6L, List.of(145L));      // 고추
        SOWING_TASK_IDS.put(5L, List.of(114L));      // 사과
        SOWING_TASK_IDS.put(2L, List.of(42L, 43L));   // 벼 (정식, 모내기)
    }

    // 파종(정식) 또는 묘목 관련 단어가 있는 영농일기 중 가장 오래된 기록의 작성일을 파종일로 반환
    public LocalDate getFirstSowingDate(Long userId, Long cropId) {
        List<Long> taskIds = SOWING_TASK_IDS.getOrDefault(cropId, List.of());
        if (taskIds.isEmpty()) {
            throw new GlobalException(ErrorCode.NOT_FOUND_SOWLING_TASK, cropId);
        }

        // 파종(정식)/묘목 관련 작업 ID 목록에 해당하는 영농일기를 작성일 기준 오름차순으로 조회
        List<FarmDiary> diaries = farmDiaryRepository.findAllSowingDiaries(userId, cropId, taskIds);
        log.info("🔍 [{}] 작물의 파종/묘목 관련 일기 개수: {}", cropId, diaries.size());
        diaries.forEach(diary ->
                log.info("📅 [{}] 날짜: {}, Task ID: {}", cropId, diary.getWriteDate(), diary.getTask().getId())
        );

        // 만약 해당하는 기록이 없으면 null 반환
        if (diaries.isEmpty()) {
            log.info("파종(정식)/묘목 관련 활동이 기록된 일기가 없습니다.");
            return null;
        }
        // 여러 건 중 가장 오래된 일기의 날짜를 파종일로 사용
        return diaries.get(0).getWriteDate();
    }

    // AI를 활용하여 예상 수확일 계산
    public String getPredictedHarvestDate(Long userId, Long cropId) {
        LocalDate sowingDate = getFirstSowingDate(userId, cropId);

        // 파종(정식)/묘목 관련 일기가 없으면 null 반환
        if (sowingDate == null) {
            return null;
        }

        // 날씨 데이터 조회
        WeatherResponse weatherResponse = weatherService.getWeatherByUserId(userId);

        // UserCrop 정보를 조회 (첫 번째 결과만 사용)
        UserCrop userCrop = userCropRepository.findFirstByUserIdAndCropId(userId, cropId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_USER_CROP));

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
                "cropName", userCrop.getCropName(),
                "sowingDate", sowingDate.toString(),
                "location", weatherResponse.getLocation().getName(),
                "weather", weatherResponse.getCurrent().getCondition().getText(),
                "temperature", weatherResponse.getCurrent().getTemp_c(),
                "humidity", weatherResponse.getCurrent().getHumidity()
        );

        // AI 서비스 호출하여 예상 수확일 반환
        return openAiService.getRecommendation(promptTemplate, variables);
    }

    // 가장 최근 일기 조회
    @Transactional(readOnly = true)
    public FarmDiaryResponse getLastDiaryEntry(Long userId, Long cropId) {
        Optional<FarmDiary> lastDiary = farmDiaryRepository
                .findTopByUserIdAndUserCrop_CropIdOrderByWriteDateDesc(userId, cropId);

        return lastDiary.map(FarmDiaryResponse::new)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_CROP_DIARY));
    }

}
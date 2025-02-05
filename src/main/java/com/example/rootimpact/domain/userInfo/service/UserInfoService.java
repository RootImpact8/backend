package com.example.rootimpact.domain.userInfo.service;

import com.example.rootimpact.domain.user.entity.User;
import com.example.rootimpact.domain.user.repository.UserRepository;
import com.example.rootimpact.domain.userInfo.dto.CropSelectionRequest;
import com.example.rootimpact.domain.userInfo.dto.LocationRequest;
import com.example.rootimpact.domain.userInfo.entity.Crop;
import com.example.rootimpact.domain.userInfo.entity.UserCrop;
import com.example.rootimpact.domain.userInfo.entity.UserLocation;
import com.example.rootimpact.domain.userInfo.repository.CropRepository;
import com.example.rootimpact.domain.userInfo.repository.UserCropRepository;
import com.example.rootimpact.domain.userInfo.repository.UserLocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserInfoService {

    private final CropRepository cropRepository;  // 기본 제공 작물 저장소
    private final UserCropRepository userCropRepository; // 사용자의 작물 저장소
    private final UserLocationRepository locationRepository; // 사용자 거주지 저장소
    private final UserRepository userRepository; // 사용자 저장소

    /**
     * ✅ 기본 제공 작물 리스트 조회
     * @return List<Crop> 기본 작물 리스트
     */
    public List<Crop> getAllCrops() {
        return cropRepository.findAll();
    }

    /**
     * ✅ 사용자 거주 지역 저장 (기존 데이터 삭제 후 새로운 데이터 저장)
     */
    public void saveUserLocation(Long userId, LocationRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        locationRepository.deleteAll(locationRepository.findByUser(user));

        UserLocation location = new UserLocation();
        location.setUser(user);
        location.setCity(request.getCity());
        location.setState(request.getState());
        location.setCountry(request.getCountry());
        locationRepository.save(location);
    }

    /**
     * ✅ 사용자 거주 지역 조회
     * @param userId 사용자 ID
     * @return UserLocation 거주 지역 정보
     */
    public UserLocation getUserLocation(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return locationRepository.findByUser(user).stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User location not found"));
    }

    /**
     * ✅ 사용자 재배 작물 및 관심 작물 저장 (최대 5개 저장, 중복 방지)
     */
    public void saveUserCrops(Long userId, CropSelectionRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<UserCrop> existingCrops = userCropRepository.findByUser(user);

        // ✅ 새로운 재배 작물 저장 (중복 방지, 최대 5개 제한)
        Optional.ofNullable(request.getCultivatedCrops()).orElse(List.of()).forEach(cropName -> {
            if (existingCrops.size() < 5 && existingCrops.stream().noneMatch(crop -> crop.getCropName().equals(cropName) && !crop.isInterestCrop())) {
                UserCrop crop = new UserCrop();
                crop.setUser(user);
                crop.setCropName(cropName);
                crop.setInterestCrop(false);
                userCropRepository.save(crop);
            }
        });

        // ✅ 새로운 관심 작물 저장 (중복 방지, 최대 5개 제한)
        Optional.ofNullable(request.getInterestCrops()).orElse(List.of()).forEach(cropName -> {
            if (existingCrops.size() < 5 && existingCrops.stream().noneMatch(crop -> crop.getCropName().equals(cropName) && crop.isInterestCrop())) {
                UserCrop crop = new UserCrop();
                crop.setUser(user);
                crop.setCropName(cropName);
                crop.setInterestCrop(true);
                userCropRepository.save(crop);
            }
        });
    }

    // ✅ **재배 작물 전체 조회 (Lazy Loading 문제 해결)**
    @Transactional(readOnly = true)
    public List<UserCrop> getCultivatedCrops(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return userCropRepository.findCultivatedCropsByUser(user);
    }

    // ✅ **관심 작물 전체 조회 (Lazy Loading 문제 해결)**
    @Transactional(readOnly = true)
    public List<UserCrop> getInterestCrops(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return userCropRepository.findInterestCropsByUser(user);
    }
    // 특정 재배 작물 조회
    public UserCrop getSpecificCultivatedCrop(Long userId, String cropName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return userCropRepository.findByUser(user).stream()
                .filter(crop -> crop.getCropName().equals(cropName) && !crop.isInterestCrop())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cultivated crop not found"));
    }

    // 특정 관심 작물 조회
    public UserCrop getSpecificInterestCrop(Long userId, String cropName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return userCropRepository.findByUser(user).stream()
                .filter(crop -> crop.getCropName().equals(cropName) && crop.isInterestCrop())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Interest crop not found"));
    }
}
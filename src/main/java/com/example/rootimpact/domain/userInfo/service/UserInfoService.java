package com.example.rootimpact.domain.userInfo.service;

import com.example.rootimpact.domain.user.entity.User;
import com.example.rootimpact.domain.user.repository.UserRepository;
import com.example.rootimpact.domain.userInfo.dto.CropSelectionRequest;
import com.example.rootimpact.domain.userInfo.dto.LocationRequest;
import com.example.rootimpact.domain.userInfo.dto.UserInfoResponse;
import com.example.rootimpact.domain.userInfo.entity.Crop;
import com.example.rootimpact.domain.userInfo.entity.UserCrop;
import com.example.rootimpact.domain.userInfo.entity.UserInfo;
import com.example.rootimpact.domain.userInfo.entity.UserLocation;
import com.example.rootimpact.domain.userInfo.repository.CropRepository;
import com.example.rootimpact.domain.userInfo.repository.UserCropRepository;
import com.example.rootimpact.domain.userInfo.repository.UserInfoRepository;
import com.example.rootimpact.domain.userInfo.repository.UserLocationRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserInfoService {

    private final CropRepository cropRepository;  // 기본 제공 작물 저장소
    private final UserCropRepository userCropRepository; // 사용자의 작물 저장소
    private final UserLocationRepository locationRepository; // 사용자 거주지 저장소
    private final UserRepository userRepository; // 사용자 저장소
    private final UserInfoRepository userInfoRepository;

    // 사용자 이름 조회
    public UserInfoResponse getUserName(Long userId) {
        UserInfo userInfo = userInfoRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("User info not found"));

        UserInfoResponse response = new UserInfoResponse();
        response.setName(userInfo.getName());

        return response;
    }

    // 사용자 이름 저장
    @Transactional
    public void saveUserName(Long userId, String name) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found")); // ✅ 유저 조회

        UserInfo userInfo = new UserInfo();
        userInfo.setUser(user);  // ✅ `user` 객체 설정
        userInfo.setName(name);

        userInfoRepository.save(userInfo);
    }

    // 사용자 이름 수정
    @Transactional
    public void updateUserName(Long userId, String name) {
        UserInfo userInfo = userInfoRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("User info not found"));

        userInfo.setName(name);
        userInfoRepository.save(userInfo);
    }

    // 기본 제공 작물 리스트 조회
    public List<Crop> getAllCrops() {
        return cropRepository.findAll();
    }

    // 사용자 거주 지역 저장 (기존 데이터 삭제 후 새로운 데이터 저장)
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

    // 사용자 거주 지역 조회
    public UserLocation getUserLocation(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return locationRepository.findByUser(user).stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User location not found"));
    }

    // 재배 작물 & 관심 작물 저장
    public void saveUserCrops(Long userId, CropSelectionRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<UserCrop> existingCrops = userCropRepository.findByUser(user);

        // 재배 작물 저장
        Optional.ofNullable(request.getCultivatedCrops()).orElse(List.of()).forEach(cropName -> {
            if (existingCrops.size() < 5 && existingCrops.stream()
                    .noneMatch(crop -> crop.getCropName().equals(cropName) && !crop.isInterestCrop())) {

                // Crop 엔티티에서 해당 작물명의 id 찾기
                Crop crop = cropRepository.findByName(cropName)
                        .orElseThrow(() -> new RuntimeException("Invalid crop name: " + cropName));

                UserCrop userCrop = new UserCrop();
                userCrop.setUser(user);
                userCrop.setCropName(cropName);
                userCrop.setCropId(crop.getId());  // cropId 설정
                userCrop.setInterestCrop(false);
                userCropRepository.save(userCrop);
            }
        });

        // 관심 작물 저장
        Optional.ofNullable(request.getInterestCrops()).orElse(List.of()).forEach(cropName -> {
            if (existingCrops.size() < 5 && existingCrops.stream()
                    .noneMatch(crop -> crop.getCropName().equals(cropName) && crop.isInterestCrop())) {

                // Crop 엔티티에서 해당 작물명의 id 찾기
                Crop crop = cropRepository.findByName(cropName)
                        .orElseThrow(() -> new RuntimeException("Invalid crop name: " + cropName));

                UserCrop userCrop = new UserCrop();
                userCrop.setUser(user);
                userCrop.setCropName(cropName);
                userCrop.setCropId(crop.getId());  // cropId 설정
                userCrop.setInterestCrop(true);
                userCropRepository.save(userCrop);
            }
        });
    }


    // 재배 작물 전체 조회
    @Transactional(readOnly = true)
    public List<UserCrop> getCultivatedCrops(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return userCropRepository.findCultivatedCropsByUser(user);
    }

    // 관심 작물 전체 조회
    @Transactional(readOnly = true)
    public List<UserCrop> getInterestCrops(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return userCropRepository.findInterestCropsByUser(user);
    }

    // 재배 작물 개별 조회
    @Transactional(readOnly = true)
    public UserCrop getSpecificCultivatedCrop(Long userId, Long cropId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return userCropRepository.findByUser(user).stream()
                .filter(crop -> crop.getCropId().equals(cropId) && !crop.isInterestCrop())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cultivated crop not found"));
    }

    // 관심 작물 개별 조회
    @Transactional(readOnly = true)
    public UserCrop getSpecificInterestCrop(Long userId, Long cropId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return userCropRepository.findByUser(user).stream()
                .filter(crop -> crop.getCropId().equals(cropId) && crop.isInterestCrop())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Interest crop not found"));
    }

    // 사용자 이메일 조회
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
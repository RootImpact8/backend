package com.example.rootimpact.domain.userInfo.controller;

import com.example.rootimpact.domain.userInfo.dto.CropSelectionRequest;
import com.example.rootimpact.domain.userInfo.dto.LocationRequest;
import com.example.rootimpact.domain.userInfo.entity.Crop;
import com.example.rootimpact.domain.userInfo.entity.UserCrop;
import com.example.rootimpact.domain.userInfo.entity.UserLocation;
import com.example.rootimpact.domain.userInfo.service.UserInfoService;
import com.example.rootimpact.domain.user.entity.User;
import com.example.rootimpact.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // 추가됨
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user-info")
public class UserInfoController {

    private final UserInfoService userInfoService;
    private final UserRepository userRepository;

    // ✅ 기본 작물 리스트 조회 (모든 사용자 접근 가능)
    @GetMapping("/crops")
    public ResponseEntity<List<Crop>> getAllCrops() {
        List<Crop> crops = userInfoService.getAllCrops();
        return ResponseEntity.ok(crops);
    }

    // ✅ 사용자 거주 지역 저장/수정 (인증 필요)
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/location")
    public ResponseEntity<String> saveUserLocation(
            @RequestBody LocationRequest request, Authentication authentication) {
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userInfoService.saveUserLocation(user.getId(), request);
        return ResponseEntity.ok("User location saved successfully.");
    }

    // ✅ 사용자 재배 작물/관심 작물 저장 (인증 필요)
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/crops")
    public ResponseEntity<String> saveUserCrops(
            @RequestBody CropSelectionRequest request, Authentication authentication) {
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userInfoService.saveUserCrops(user.getId(), request);
        return ResponseEntity.ok("User crops saved successfully.");
    }

    // ✅ 사용자 거주 지역 조회 (인증 필요)
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/location")
    public ResponseEntity<UserLocation> getUserLocation(Authentication authentication) {
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserLocation location = userInfoService.getUserLocation(user.getId());
        return ResponseEntity.ok(location);
    }

    // ✅ 사용자가 선택한 모든 재배 작물 및 관심 작물 조회 (인증 필요)
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/user-crops")
    public ResponseEntity<Map<String, List<String>>> getUserCrops(Authentication authentication) {
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<UserCrop> cultivatedCrops = userInfoService.getCultivatedCrops(user.getId());
        List<UserCrop> interestCrops = userInfoService.getInterestCrops(user.getId());

        Map<String, List<String>> cropsResponse = Map.of(
                "cultivatedCrops", cultivatedCrops.stream().map(UserCrop::getCropName).collect(Collectors.toList()),
                "interestCrops", interestCrops.stream().map(UserCrop::getCropName).collect(Collectors.toList())
        );

        return ResponseEntity.ok(cropsResponse);
    }

    // ✅ **재배 작물 전체 조회**

    @Transactional(readOnly = true)
    @GetMapping("/crops/cultivated")
    public ResponseEntity<List<UserCrop>> getAllCultivatedCrops(Authentication authentication) {
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<UserCrop> crops = userInfoService.getCultivatedCrops(user.getId());
        return ResponseEntity.ok(crops);
    }

    // ✅ **관심 작물 전체 조회**

    @Transactional(readOnly = true)
    @GetMapping("/crops/interest")
    public ResponseEntity<List<UserCrop>> getAllInterestCrops(Authentication authentication) {
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<UserCrop> crops = userInfoService.getInterestCrops(user.getId());
        return ResponseEntity.ok(crops);
    }

    // ✅ 특정 재배 작물 개별 조회 (인증 필요)
    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/crops/cultivated/{cropName}")
    public ResponseEntity<UserCrop> getSpecificCultivatedCrop(
            @PathVariable("cropName") String cropName, Authentication authentication) {
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserCrop crop = userInfoService.getSpecificCultivatedCrop(user.getId(), cropName);
        return ResponseEntity.ok(crop);
    }

    // ✅ 특정 관심 작물 개별 조회 (인증 필요)
    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/crops/interest/{cropName}")
    public ResponseEntity<UserCrop> getSpecificInterestCrop(
            @PathVariable("cropName") String cropName, Authentication authentication) {
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserCrop crop = userInfoService.getSpecificInterestCrop(user.getId(), cropName);
        return ResponseEntity.ok(crop);
    }
}
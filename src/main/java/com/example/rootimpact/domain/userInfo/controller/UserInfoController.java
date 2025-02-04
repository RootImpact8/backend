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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user-info")
public class UserInfoController {

    private final UserInfoService userInfoService;
    private final UserRepository userRepository; // ✅ 이메일을 기반으로 userId 찾기 위해 추가
    // 기본 작물 리스트 조회
    @GetMapping("/crops")
    public ResponseEntity<List<Crop>> getAllCrops() {
        List<Crop> crops = userInfoService.getAllCrops();
        return ResponseEntity.ok(crops);
    }

    /**
     * ✅ 사용자 거주 지역 저장/수정 (JWT에서 userId 가져오기)
     */
    @PostMapping("/location")
    public ResponseEntity<String> saveUserLocation(@RequestBody LocationRequest request, Authentication authentication) {
        String userEmail = authentication.getName(); // JWT에서 이메일 가져오기
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found")); // 이메일 기반으로 사용자 조회

        userInfoService.saveUserLocation(user.getId(), request);
        return ResponseEntity.ok("User location saved successfully.");
    }

    /**
     * ✅ 사용자 재배 작물/관심 작물 저장 (JWT에서 userId 가져오기)
     */
    @PostMapping("/crops")
    public ResponseEntity<String> saveUserCrops(@RequestBody CropSelectionRequest request, Authentication authentication) {
        String userEmail = authentication.getName(); // JWT에서 이메일 가져오기
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found")); // 이메일 기반으로 사용자 조회

        userInfoService.saveUserCrops(user.getId(), request);
        return ResponseEntity.ok("User crops saved successfully.");
    }
    @GetMapping("/location")
    public ResponseEntity<UserLocation> getUserLocation(Authentication authentication) {
        String userEmail = authentication.getName(); // JWT에서 이메일 가져오기
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found")); // 이메일 기반으로 사용자 조회

        UserLocation location = userInfoService.getUserLocation(user.getId());
        return ResponseEntity.ok(location);
    }
    @GetMapping("/user-crops")
    public ResponseEntity<Map<String, List<String>>> getUserCrops(Authentication authentication) {
        String userEmail = authentication.getName(); // JWT에서 이메일 가져오기
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found")); // 이메일 기반으로 사용자 조회

        List<UserCrop> cultivatedCrops = userInfoService.getCultivatedCrops(user.getId());
        List<UserCrop> interestCrops = userInfoService.getInterestCrops(user.getId());

        Map<String, List<String>> cropsResponse = Map.of(
                "cultivatedCrops", cultivatedCrops.stream().map(UserCrop::getCropName).collect(Collectors.toList()),
                "interestCrops", interestCrops.stream().map(UserCrop::getCropName).collect(Collectors.toList())
        );

        return ResponseEntity.ok(cropsResponse);
    }

}
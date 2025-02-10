package com.example.rootimpact.domain.userInfo.controller;

import com.example.rootimpact.domain.userInfo.dto.CropSelectionRequest;
import com.example.rootimpact.domain.userInfo.dto.LocationRequest;
import com.example.rootimpact.domain.userInfo.entity.Crop;
import com.example.rootimpact.domain.userInfo.entity.UserCrop;
import com.example.rootimpact.domain.userInfo.entity.UserLocation;
import com.example.rootimpact.domain.userInfo.service.UserInfoService;
import com.example.rootimpact.domain.user.entity.User;
import com.example.rootimpact.domain.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user-info")
@Tag(name = "UserInfo", description = "사용자 정보 관련 API")
public class UserInfoController {

    private final UserInfoService userInfoService;
    private final UserRepository userRepository;

    @Operation(summary = "기본 작물 리스트 조회", description = "모든 사용자가 접근 가능한 기본 작물 리스트를 조회합니다.")
    @GetMapping("/crops")
    public ResponseEntity<List<Crop>> getAllCrops() {
        List<Crop> crops = userInfoService.getAllCrops();
        return ResponseEntity.ok(crops);
    }

    @Operation(summary = "사용자 거주 지역 저장/수정",
            description = "인증된 사용자의 거주 지역 정보를 저장 또는 수정합니다.")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/location")
    public ResponseEntity<String> saveUserLocation(
            @Parameter(description = "사용자 거주 지역 요청 객체", required = true)
            @RequestBody LocationRequest request,
            @Parameter(hidden = true) Authentication authentication) {
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userInfoService.saveUserLocation(user.getId(), request);
        return ResponseEntity.ok("User location saved successfully.");
    }

    @Operation(summary = "사용자 재배 작물/관심 작물 저장",
            description = "인증된 사용자의 재배 작물 및 관심 작물 정보를 저장합니다.")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/crops")
    public ResponseEntity<String> saveUserCrops(
            @Parameter(description = "사용자 작물 선택 요청 객체", required = true)
            @RequestBody CropSelectionRequest request,
            @Parameter(hidden = true) Authentication authentication) {
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userInfoService.saveUserCrops(user.getId(), request);
        return ResponseEntity.ok("User crops saved successfully.");
    }

    @Operation(summary = "사용자 거주 지역 조회",
            description = "인증된 사용자의 거주 지역 정보를 조회합니다.")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/location")
    public ResponseEntity<UserLocation> getUserLocation(
            @Parameter(hidden = true) Authentication authentication) {
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserLocation location = userInfoService.getUserLocation(user.getId());
        return ResponseEntity.ok(location);
    }

    @Operation(summary = "사용자가 선택한 재배 작물 및 관심 작물 조회",
            description = "인증된 사용자가 선택한 모든 재배 작물 및 관심 작물 목록을 조회합니다.")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/user-crops")
    public ResponseEntity<Map<String, List<String>>> getUserCrops(
            @Parameter(hidden = true) Authentication authentication) {
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

    @Operation(summary = "사용자 재배 작물만 전체 조회",
            description = "인증된 사용자의 모든 재배 작물 목록을 조회합니다.")
    @Transactional(readOnly = true)
    @GetMapping("/crops/cultivated")
    public ResponseEntity<List<UserCrop>> getAllCultivatedCrops(
            @Parameter(hidden = true) Authentication authentication) {
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<UserCrop> crops = userInfoService.getCultivatedCrops(user.getId());
        return ResponseEntity.ok(crops);
    }

    @Operation(summary = "사용자 관심 작물만 전체 조회",
            description = "인증된 사용자의 모든 관심 작물 목록을 조회합니다.")
    @Transactional(readOnly = true)
    @GetMapping("/crops/interest")
    public ResponseEntity<List<UserCrop>> getAllInterestCrops(
            @Parameter(hidden = true) Authentication authentication) {
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<UserCrop> crops = userInfoService.getInterestCrops(user.getId());
        return ResponseEntity.ok(crops);
    }

    @Operation(summary = "사용자 재배 작물 개별 조회",
            description = "인증된 사용자가 선택한 특정 재배 작물을 조회합니다.")
    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/crops/cultivated/{cropName}")
    public ResponseEntity<UserCrop> getSpecificCultivatedCrop(
            @Parameter(description = "조회할 재배 작물명", required = true, example = "Tomato")
            @PathVariable("cropName") String cropName,
            @Parameter(hidden = true) Authentication authentication) {
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserCrop crop = userInfoService.getSpecificCultivatedCrop(user.getId(), cropName);
        return ResponseEntity.ok(crop);
    }

    @Operation(summary = "사용자 관심 작물 개별 조회",
            description = "인증된 사용자가 선택한 특정 관심 작물을 조회합니다.")
    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/crops/interest/{cropName}")
    public ResponseEntity<UserCrop> getSpecificInterestCrop(
            @Parameter(description = "조회할 관심 작물명", required = true, example = "Carrot")
            @PathVariable("cropName") String cropName,
            @Parameter(hidden = true) Authentication authentication) {
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserCrop crop = userInfoService.getSpecificInterestCrop(user.getId(), cropName);
        return ResponseEntity.ok(crop);
    }
}
package com.example.rootimpact.domain.diary.repository;

import com.example.rootimpact.domain.diary.entity.FarmDiary;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FarmDiaryRepository extends JpaRepository<FarmDiary, Long> {
    // 사용자 ID로 일기 목록 조회
    List<FarmDiary> findByUserId(Long userId);
    // 작성일로 일기 목록 조회
    List<FarmDiary> findByWriteDate(LocalDate writeDate);
    // 작물별로 일기 조회
    List<FarmDiary> findByUserCrop_CropName(String cropName);
    // ✅ 특정 사용자의 특정 작물에 대한 첫 번째 일기 찾기 (파종일)
    Optional<FarmDiary> findTopByUserIdAndUserCrop_CropNameOrderByWriteDateAsc(Long userId, String cropName);
    // ✅ 특정 사용자의 특정 작물에 대한 "가장 최근 일기" 1개만 조회 (최신순 정렬 후 첫 번째 데이터)
    Optional<FarmDiary> findTopByUserIdAndUserCrop_CropNameOrderByWriteDateDesc(Long userId, String cropName);
    //오름차순 일기 - 영농일기 ai재배활동을위해서
    List<FarmDiary> findByUserIdAndUserCrop_CropNameOrderByWriteDateAsc(Long userId, String cropName);

}

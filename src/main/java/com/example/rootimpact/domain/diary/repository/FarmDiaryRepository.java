package com.example.rootimpact.domain.diary.repository;

import com.example.rootimpact.domain.diary.entity.FarmDiary;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.example.rootimpact.domain.userInfo.entity.UserCrop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FarmDiaryRepository extends JpaRepository<FarmDiary, Long> {
    // 사용자 ID로 일기 목록 조회
    List<FarmDiary> findByUserId(Long userId);
    // 작성일로 일기 목록 조회
    List<FarmDiary> findByWriteDate(LocalDate writeDate);
    List<FarmDiary> findByUserCrop_CropId(Long cropId);

    // ✅ 특정 사용자의 특정 작물에 대한 첫 번째 일기 찾기 (파종일)
    Optional<FarmDiary> findTopByUserIdAndUserCrop_CropIdOrderByWriteDateAsc(Long userId, Long cropId);
    // ✅ 특정 사용자의 특정 작물에 대한 "가장 최근 일기" 1개만 조회 (최신순 정렬 후 첫 번째 데이터)
    Optional<FarmDiary> findTopByUserIdAndUserCrop_CropIdOrderByWriteDateDesc(Long userId, Long cropId);
    //오름차순 일기 - 영농일기 ai재배활동을위해서
    List<FarmDiary> findByUserIdAndUserCrop_CropIdOrderByWriteDateAsc(Long userId, Long cropId);

    // ✅ 특정 작물의 "파종(정식)" 관련 활동을 포함한 일기 전체 조회 (오름차순 정렬)
    @Query("SELECT fd FROM FarmDiary fd " +
            "WHERE fd.user.id = :userId " +
            "AND fd.userCrop.cropId = :cropId " +
            "AND fd.task.id IN :taskIds " +
            "ORDER BY fd.writeDate ASC")
    List<FarmDiary> findAllSowingDiaries(
            @Param("userId") Long userId,
            @Param("cropId") Long cropId,
            @Param("taskIds") List<Long> taskIds

    );
}

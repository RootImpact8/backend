package com.example.rootimpact.domain.diary.repository;

import com.example.rootimpact.domain.diary.entity.FarmDiary;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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

    // 사용자 재배 작물 조회
    List<FarmDiary> findByUserCrop_CropId(Long cropId);

    // 특정 사용자의 특정 작물에 대한 첫 번째 일기 찾기 (파종일)
    Optional<FarmDiary> findTopByUserIdAndUserCrop_CropIdOrderByWriteDateAsc(Long userId, Long cropId);

    // 특정 사용자의 특정 작물에 대한 "가장 최근 일기" 1개만 조회 (최신순 정렬 후 첫 번째 데이터)
    Optional<FarmDiary> findTopByUserIdAndUserCrop_CropIdOrderByWriteDateDesc(Long userId, Long cropId);

    @Query("SELECT DISTINCT d FROM FarmDiary d " +
            "LEFT JOIN FETCH d.task " +
            "WHERE d.userCrop.cropId = :cropId " +
            "AND d.user.id = :userId " +
            "ORDER BY d.writeDate ASC")
    List<FarmDiary> findDiariesWithTask(@Param("userId") Long userId, @Param("cropId") Long cropId);


    // 특정 작물의 "파종(정식)" 관련 활동을 포함한 일기 전체 조회 (오름차순 정렬)
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

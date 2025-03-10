package com.example.rootimpact.domain.userInfo.repository;


import com.example.rootimpact.domain.user.entity.User;
import com.example.rootimpact.domain.userInfo.entity.UserCrop;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCropRepository extends JpaRepository<UserCrop, Long> {
    Optional<UserCrop> findFirstByUserIdAndCropId(Long userId, Long cropId);
    // 특정 사용자의 모든 작물 데이터 조회
    List<UserCrop> findByUser(User user);

    // 특정 사용자의 재배 작물(CULTIVATED) 조회
    default List<UserCrop> findCultivatedCropsByUser(User user) {
        return findByUser(user).stream()
                .filter(crop -> !crop.isInterestCrop())
                .toList();
    }

    // 특정 사용자의 관심 작물(INTEREST) 조회
    default List<UserCrop> findInterestCropsByUser(User user) {
        return findByUser(user).stream()
                .filter(UserCrop::isInterestCrop)
                .toList();
    }

    Optional<UserCrop> findById(Long id);

    Optional<UserCrop> findByUserIdAndCropId(Long userId, Long cropId);

    Optional<UserCrop> findByUserIdAndCropIdAndIsInterestCropTrue(Long userId, Long cropId);

}

package com.example.rootimpact.domain.userInfo.repository;



import com.example.rootimpact.domain.user.entity.User;
import com.example.rootimpact.domain.userInfo.entity.UserLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserLocationRepository extends JpaRepository<UserLocation, Long> {

    /**
     * 특정 사용자의 모든 거주 지역 데이터 조회
     * @param user User 엔티티
     * @return List<UserLocation> 사용자의 지역 데이터 리스트
     */
    List<UserLocation> findByUser(User user);

    /**
     * 특정 사용자의 첫 번째 거주 지역 조회
     * @param user User 엔티티
     * @return UserLocation 사용자의 거주 지역 데이터 (옵션)
     */
    default UserLocation findFirstByUser(User user) {
        return findByUser(user).stream().findFirst().orElse(null);
    }
}

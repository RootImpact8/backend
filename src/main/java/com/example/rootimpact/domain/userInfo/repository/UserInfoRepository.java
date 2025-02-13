package com.example.rootimpact.domain.userInfo.repository;

import com.example.rootimpact.domain.userInfo.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {
    // 사용자 ID로 UserInfo 조회
    Optional<UserInfo> findByUser_Id(Long userId);
}

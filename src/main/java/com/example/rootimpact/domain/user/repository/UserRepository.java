package com.example.rootimpact.domain.user.repository;

import com.example.rootimpact.domain.user.entity.User;
import com.example.rootimpact.domain.userInfo.entity.UserCrop;
import jakarta.persistence.Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);

}

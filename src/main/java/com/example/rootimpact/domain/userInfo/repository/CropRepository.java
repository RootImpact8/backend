package com.example.rootimpact.domain.userInfo.repository;

import com.example.rootimpact.domain.userInfo.entity.Crop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CropRepository extends JpaRepository<Crop, Long> {
    Optional<Crop> findByName(String name);
}
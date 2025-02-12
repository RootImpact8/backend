package com.example.rootimpact.domain.diary.repository;

import com.example.rootimpact.domain.diary.entity.Task;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByCropId(Long cropId);

}
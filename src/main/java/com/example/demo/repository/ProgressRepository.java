package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import com.example.demo.domain.Progress;

@Repository
public interface ProgressRepository extends JpaRepository<Progress, Integer> {

    @Query(value="SELECT * FROM Progress WHERE taskId=:taskId ORDER BY modifiedDate", nativeQuery = true)
    List<Progress> findAllByTaskId(Integer taskId);
    
}

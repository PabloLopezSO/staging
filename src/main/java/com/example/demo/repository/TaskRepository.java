package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.domain.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

	List<Task> findAllByOrderByIdAsc();
	@Query("SELECT new Task ( id, title, status, dueDate,createdDate ) FROM Task task  ORDER BY createdDate ASC")
	Page<Task> findAllOrderByCreatedDateAsc(Pageable pageable);
	Optional<Task> findByTitle(String title);
	Optional<Task> findById(Integer id);
	@Query(value="SELECT * FROM Tasks WHERE title LIKE %:keyword% OR description LIKE %:keyword% ", nativeQuery = true)
	Page<Task> findByKeyword(String keyword, Pageable pageable);
	@Query(value="SELECT * FROM Tasks WHERE (title LIKE  %:keyword% OR description LIKE  %:keyword% ) AND status = :status ", nativeQuery = true)
	Page<Task> findByKeywordAndStatus(String keyword, Integer status, Pageable pageable);
	Optional<Task> findTaskById(Integer id);
	
}

package com.example.demo.repository;

import java.util.Optional;

import com.example.demo.domain.User;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, Integer>{
    
    Optional<User> findByMail(String mail);
    Optional<User> findById(Integer id);
    @Query(value="SELECT mail FROM Users WHERE id=:id", nativeQuery = true)
    String findNameById(Integer id);


}

package com.example.repository;

import com.example.entity.TeacherAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherAccountRepository extends JpaRepository<TeacherAccount, String> {
    boolean existsByTeacherId(String teacherId);
} 
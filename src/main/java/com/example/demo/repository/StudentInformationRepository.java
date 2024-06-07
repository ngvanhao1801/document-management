package com.example.demo.repository;

import com.example.demo.entity.StudentInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentInformationRepository extends JpaRepository<StudentInformation, Long> {

	boolean existsByStudentCode(Long studentCode);

}

package com.example.demo.repository;

import com.example.demo.entity.StudentInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentInformationRepository extends JpaRepository<StudentInformation, Long> {

	boolean existsByStudentCode(Long studentCode);

	Optional<StudentInformation> findByEmail(String email);

}

package com.example.demo.service;

import com.example.demo.entity.StudentInformation;
import com.example.demo.repository.StudentInformationRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class StudentInformationService {

  private final StudentInformationRepository studentInformationRepository;

  public StudentInformationService(StudentInformationRepository studentInformationRepository) {
    this.studentInformationRepository = studentInformationRepository;
  }

  public void saveStudentToDatabase(MultipartFile file) {
    if (ExcelUploadService.isValidExcelFile(file)) {
      try {
        List<StudentInformation> studentInformations = ExcelUploadService.getStudentsDataFromExcel(file.getInputStream());

        for (StudentInformation student : studentInformations) {
          Optional<StudentInformation> existingStudent = studentInformationRepository.findByEmail(student.getEmail());
          if (existingStudent.isPresent()) {
            // Cập nhật bản ghi hiện có
            StudentInformation existing = existingStudent.get();
            existing.setName(student.getName());
            existing.setStudentCode(student.getStudentCode());
            existing.setClassroom(student.getClassroom());
            existing.setMajors(student.getMajors());
            existing.setYearOfAdmission(student.getYearOfAdmission());
            existing.setStatusStudent(student.getStatusStudent());
            existing.setGender(student.getGender());
            existing.setAddress(student.getAddress());
            existing.setBirthday(student.getBirthday());
            studentInformationRepository.save(existing);
          } else {
            // Lưu mới nếu không tồn tại
            studentInformationRepository.save(student);
          }
        }
      } catch (IOException e) {
        throw new IllegalArgumentException("The file is not a valid excel file");
      }
    }
  }
}


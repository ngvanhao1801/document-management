package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "student_information", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentInformation implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long studentCode;

  private String name;

  private String email;

  @Temporal(TemporalType.DATE)
  private Date birthday;

  private String gender;

  private String classroom;

  private String majors;

  private Integer yearOfAdmission;

  private String address;

  private String statusStudent;

}

package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

  private Long userId;

  private String name;

  private String email;

  @Temporal(TemporalType.DATE)
  private Date registerDate;

  private boolean status;

  private String roleName;

}

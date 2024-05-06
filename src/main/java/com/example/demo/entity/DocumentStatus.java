package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "document_status")
public class DocumentStatus {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  private String statusName;

}

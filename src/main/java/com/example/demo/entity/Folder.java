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
@Table(name = "folder")
public class Folder {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int folderId;

  private String folderName;

  private String description;

}

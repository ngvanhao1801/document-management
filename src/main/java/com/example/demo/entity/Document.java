package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "document")
public class Document {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  private String title;

  private String description;

  private int authorId;

  @Temporal(TemporalType.DATE)
  private Date uploadDate;

  @ManyToOne
  @JoinColumn(name = "folderId")
  private Folder folderId;

  private int statusId;

}

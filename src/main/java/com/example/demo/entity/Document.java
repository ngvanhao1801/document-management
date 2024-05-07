package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "document")
public class Document implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  private String documentName;

  @Column(name = "description",columnDefinition = "TEXT")
  private String description;

  @ManyToOne
  @JoinColumn(name = "userId")
  private User user;

  @Temporal(TemporalType.DATE)
  private Date uploadDate;

  @ManyToOne
  @JoinColumn(name = "folderId")
  private Folder folder;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "statusId")
  private DocumentStatus documentStatus;

  private String documentImage;

  @OneToMany(mappedBy = "document")
  private List<Version> versions;

}

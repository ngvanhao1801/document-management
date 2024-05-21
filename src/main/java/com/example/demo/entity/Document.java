package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
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
  private Long id;

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

  private String documentFile;

  private String mediaType;

  public boolean favorite;

  @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Feedback> feedbacks = new ArrayList<>();

}

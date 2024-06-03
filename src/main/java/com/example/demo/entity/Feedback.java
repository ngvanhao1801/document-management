package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "feedback")
public class Feedback implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @ManyToOne
  @JoinColumn(name = "documentId")
  private Document document;

  @ManyToOne
  @JoinColumn(name = "userId")
  private User userId;

  @Column(name = "comment",columnDefinition = "TEXT")
  private String comment;

  private String rate;

  @Temporal(TemporalType.TIMESTAMP)
  private Date createdAt;

}

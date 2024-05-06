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
@Table(name = "feedback")
public class Feedback {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  private int documentId;

  @ManyToOne
  @JoinColumn(name = "userId")
  private User userId;

  private String comment;

  private String rate;

}

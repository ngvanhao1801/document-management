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
@Table(name = "edit_history")
public class EditHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  private int versionId;

  @Temporal(TemporalType.DATE)
  private Date editDate;

  @ManyToOne
  @JoinColumn(name = "userId")
  private User userId;

  private String description;

}

package com.example.demo.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

@Entity
@Table(name = "user", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long userId;
  private String name;
  private String email;
  private String password;
  private String avatar;
  @Temporal(TemporalType.DATE)
  private Date registerDate;
  private Boolean status;

  @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  @JoinTable(name = "users_roles",
      joinColumns = @JoinColumn(name = "user_id",
          referencedColumnName = "userId"),
      inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))

  private Collection<Role> roles;

}

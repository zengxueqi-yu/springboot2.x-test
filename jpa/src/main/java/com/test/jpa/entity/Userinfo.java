package com.test.jpa.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "userinfo")
public class Userinfo {

  @Id
  @GeneratedValue
  private long id;
  @Column(name = "username",nullable = false)
  private String username;
  private String password;
  private String pwdsalt;
  private String mobile;

}

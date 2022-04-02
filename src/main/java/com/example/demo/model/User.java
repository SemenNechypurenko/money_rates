package com.example.demo.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name="users", schema="public")
@Data
public class User {
    @Id
    @Column(name="id")
    private String id;

    @Column(name="name")
    private String name;

    @Column(name="second_name")
    private String secondName;

    @Column(name="email")
    private String email;

    @Column(name="login")
    private String login;

    @Column(name="date_of_birth")
    private Date dateOfBirth;

    @Column(name="date_of_registration")
    private Date dateOfRegistration;
}

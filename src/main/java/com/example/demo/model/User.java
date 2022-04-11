package com.example.demo.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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

    @Column(name="password")
    private String password;

    @Column(name="date_of_birth")
    private Date dateOfBirth;

    @Column(name="date_of_registration")
    private Date dateOfRegistration;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name="user_roles",
            joinColumns=@JoinColumn(name="user_id"),
            inverseJoinColumns=@JoinColumn(name="role_id"))
    private Set<Role> roles = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private Set<Subscription> subscriptions = new HashSet<>();
}

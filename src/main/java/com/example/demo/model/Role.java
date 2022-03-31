package com.example.demo.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name="roles", schema="public")
@Data
public class Role {

    @Id
    @Column(name="id")
    private String id;

    @Column(name="title")
    private String title;
}

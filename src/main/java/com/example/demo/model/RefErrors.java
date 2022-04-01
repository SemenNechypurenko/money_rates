package com.example.demo.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="ref_errors", schema="public")
@Data
public class RefErrors {

    @Id
    @Column(name="id")
    private String id;

    @Column(name="number")
    private Long number;

    @Column(name="english")
    private String english;

    @Column(name="russian")
    private String russian;

    @Column(name="code")
    private String code;
}

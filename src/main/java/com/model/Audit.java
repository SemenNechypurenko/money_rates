package com.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name="audit", schema="public")
@Data
public class Audit {
    @Id
    @Column(name="id")
    private String id;

    @Column(name="method_name")
    private String methodName;

    @Column(name="user_name")
    private String userName;

    @Column(name="user_id")
    private String userId;

    @Column(name="date_of_execution")
    private Date dateOfExecution;
}



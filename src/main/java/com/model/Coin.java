package com.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="money_rates", schema="public")
@ToString
public class Coin {
    @Id
    @Column(name="id")
    private String id;
    @Column(name="title")
    private String title;
    @Column(name="market")
    private String market;
    @Column(name="price")
    private Double price;
    @Column(name="currency")
    private String currency;
    @Column(name="date")
    private Date date;
}

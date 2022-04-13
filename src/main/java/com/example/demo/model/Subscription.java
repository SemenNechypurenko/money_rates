package com.example.demo.model;

import com.example.demo.model.enums.Timeline;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="subscriptions", schema="public")
@Data
public class Subscription {
    @Id
    @Column(name="id")
    private String id;


    @Column(name="user_id", insertable = false, updatable = false)
    private String userId;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="user_id", nullable=false)
    private User user;


    @Column(name = "currency", nullable=false)
    private String currency;


    // delta of percent to be changed before informing a user via email
    @Column(name = "date_of_subscription", nullable=false)
    private Date dateOfSubscription;

    // percentage change over a specified time period to send to email
    @Column(name = "delta_change", nullable=false)
    private Integer delta ;


    // inform a user via email after period
    @Column(name = "period", nullable=false)
    private Integer period;


    // user to be informed via email after period * timeline (minute, hour, day)
    @Enumerated(EnumType.STRING)
    @Column(name = "timeline", nullable=false)
    private Timeline timeline;
}

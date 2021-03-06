package com.dto.response;

import com.model.enums.Timeline;
import lombok.Data;

import java.util.Date;

@Data
public class SubscriptionResponseDto {
    private String id;
    private String userId;
    private String currency;
    private Date dateOfSubscription;
    private Integer delta ;
    private Integer period;
    private Timeline timeline;
}

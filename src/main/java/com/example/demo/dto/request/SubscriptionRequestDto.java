package com.example.demo.dto.request;

import com.example.demo.model.enums.Timeline;
import lombok.Data;

@Data
public class SubscriptionRequestDto {
    private String id;
    private String login;
    private String currency;
    // delta of percent to be changed before informing a user via email
    private Integer deltaChange;
    // inform a user via email after period
    private Integer period;
    // user to be informed via email after period * timeline (minute, hour, day)
    private String timeline;
}

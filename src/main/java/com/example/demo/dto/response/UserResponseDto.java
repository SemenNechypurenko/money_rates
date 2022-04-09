package com.example.demo.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


import java.util.Date;
import java.util.HashSet;
import java.util.Set;
@Data
public class UserResponseDto {
    private String id;
    private String name;
    @JsonProperty("second_name")
    private String secondName;
    private String email;
    private String login;
    private String password;
    @JsonProperty("date_of_birth")
    private Date dateOfBirth;
    @JsonProperty("date_of_registration")
    private Date dateOfRegistration;
    private Set<RoleResponseDto> roles = new HashSet<>();
}

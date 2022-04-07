package com.example.demo.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.Set;

@Data
public class UserUpdateRequestDto {
    private String name;
    @JsonProperty("second_name")
    private String secondName;
    @JsonProperty("date_of_birth")
    private Date dateOfBirth;
    private Set<String> roles;
}
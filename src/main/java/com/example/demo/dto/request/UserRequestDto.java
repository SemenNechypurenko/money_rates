package com.example.demo.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.Set;

@Data
@NoArgsConstructor
public class UserRequestDto {

    @NotBlank(message = "not empty")
    private String name;

    @NotBlank
    @JsonProperty("second_name")
    private String secondName;

    @NotBlank @Email
    private String email;

    @NonNull
    @JsonProperty("date_of_birth")
    private Date dateOfBirth;

    @NotBlank
    private String login;

    @NotBlank
    private String password;

    private Set<String> roles;
}

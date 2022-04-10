package com.example.demo.dto.response;

import lombok.Data;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
@Data
public class UserResponseDto {
    private String id;
    private String name;
    private String secondName;
    private String email;
    private String login;
    private String password;
    private Date dateOfBirth;
    private Date dateOfRegistration;
    private Set<RoleResponseDto> roles = new HashSet<>();
}

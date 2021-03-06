package com.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class RoleRequestDto {
    @NotBlank
    private String title;
}

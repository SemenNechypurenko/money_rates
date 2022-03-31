package com.example.demo.controller;

import com.example.demo.dto.request.RoleRequestDto;
import com.example.demo.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("roles")
public class RoleController {
    private final RoleService service;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody RoleRequestDto dto) {
        service.create(dto);
        return ResponseEntity.ok().build();
    }
}

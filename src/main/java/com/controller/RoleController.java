package com.controller;

import com.dto.request.RoleRequestDto;
import com.dto.response.RoleResponseDto;
import com.exception.RoleMoneyRateException;
import com.model.Role;
import com.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("roles")
public class RoleController {
    private final RoleService service;
    private final ModelMapper mapper;

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody RoleRequestDto dto) {
        try{
            RoleResponseDto response = serialize(service.create(dto));
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RoleMoneyRateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @GetMapping
    public Set<RoleResponseDto> list() {
        return service.list().stream()
                .map(role -> mapper.map(role, RoleResponseDto.class))
                .collect(Collectors.toSet());
    }

    private RoleResponseDto serialize(Role role) {
        return mapper.map(role, RoleResponseDto.class);
    }
}

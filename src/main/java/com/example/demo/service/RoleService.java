package com.example.demo.service;

import com.example.demo.dto.request.RoleRequestDto;
import com.example.demo.model.Role;
import com.example.demo.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository repository;
    private final ModelMapper mapper;

    @Transactional
    public Role create(RoleRequestDto dto) {
        Role role = new Role();
        mapper.map(dto, role);
        return repository.save(role);
    }

    @PostConstruct
    public void convert() {
        Converter<RoleRequestDto, Role> toEntity = mappingContext -> {
            Role role = mappingContext.getDestination();
            role.setId(UUID.randomUUID().toString());
//            role.setTitle(mappingContext.getSource().getTitle());
            return role;
        };
        mapper.createTypeMap(RoleRequestDto.class, Role.class).setPostConverter(toEntity);
    }
}

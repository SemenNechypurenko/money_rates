package com.example.demo.service;

import com.example.demo.dto.request.RoleRequestDto;
import com.example.demo.exception.RoleMoneyRateException;
import com.example.demo.model.Role;
import com.example.demo.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
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
    public Role create(RoleRequestDto dto) throws RoleMoneyRateException {
        validateOnCreateOrUpdate(dto);
        Role role = new Role();
        mapper.map(dto, role);
        return repository.save(role);
    }
    private void validateOnCreateOrUpdate(RoleRequestDto dto) throws RoleMoneyRateException {
        if (CollectionUtils.isNotEmpty(repository.findByTitle(dto.getTitle()))) {
            throw new RoleMoneyRateException("Такой пользователь уже существует");
        }
    }

    @PostConstruct
    public void convert() {
        Converter<RoleRequestDto, Role> toEntity = mappingContext -> {
            Role role = mappingContext.getDestination();
            role.setId(UUID.randomUUID().toString());
            return role;
        };
        mapper.createTypeMap(RoleRequestDto.class, Role.class).setPostConverter(toEntity);
    }
}

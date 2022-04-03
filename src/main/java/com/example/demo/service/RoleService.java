package com.example.demo.service;

import com.example.demo.dto.request.RoleRequestDto;
import com.example.demo.exception.RoleMoneyRateException;
import com.example.demo.exception.UserMoneyRateException;
import com.example.demo.model.RefErrors;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.RefErrorsRepository;
import com.example.demo.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository repository;
    private final RefErrorsRepository errorsRepository;
    private final ModelMapper mapper;

    @Transactional
    public Role create(RoleRequestDto dto) throws RoleMoneyRateException {
        validateOnCreateOrUpdate(dto);
        Role role = new Role();
        mapper.map(dto, role);
        return repository.save(role);
    }
    private void validateOnCreateOrUpdate(RoleRequestDto dto) throws RoleMoneyRateException {
        if (repository.findRoleByTitle(dto.getTitle()).isPresent()) {
            throwException(1L);
        }
    }

    public Role findByTitle(String title) throws RoleMoneyRateException {
        Role role = repository.findRoleByTitle(title).orElse(null);
        if (Objects.isNull(role)) {
            throwException(2L);
        }
        return role;
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

    public void throwException(Long exceptionNumber) throws RoleMoneyRateException {
        RefErrors error = errorsRepository.findByNumber(exceptionNumber).orElse(null);
        assert Objects.nonNull(error);
        throw new RoleMoneyRateException(error.getRussian(), error.getEnglish(), error.getCode(), error.getNumber());
    }
}

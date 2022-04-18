package com.service;

import com.dto.request.RoleRequestDto;
import com.dto.response.RoleResponseDto;
import com.exception.RoleMoneyRateException;
import com.model.RefErrors;
import com.model.Role;
import com.repository.RefErrorsRepository;
import com.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;
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
        Role role = mapper.map(dto, Role.class);
        return repository.save(role);
    }
    private void validateOnCreateOrUpdate(RoleRequestDto dto) throws RoleMoneyRateException {
        if (repository.findRoleByTitle(dto.getTitle()).isPresent()) {
            throwException(1L);
        }
    }
    public void throwException(Long exceptionNumber) throws RoleMoneyRateException {
        RefErrors error = errorsRepository.findByNumber(exceptionNumber).orElse(null);
        assert Objects.nonNull(error);
        throw new RoleMoneyRateException(error.getRussian(), error.getEnglish(), error.getCode(), error.getNumber());
    }

    public List<Role> list() {
        return repository.findAll();
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

        Converter<Role, RoleResponseDto> fromEntity = MappingContext::getDestination;

        mapper.createTypeMap(RoleRequestDto.class, Role.class).setPostConverter(toEntity);
        mapper.createTypeMap(Role.class, RoleResponseDto.class).setPostConverter(fromEntity);

    }
}

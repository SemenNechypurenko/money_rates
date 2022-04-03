package com.example.demo.service;

import com.example.demo.dto.request.UserRequestDto;
import com.example.demo.dto.request.UserUpdateRequestDto;
import com.example.demo.exception.RoleMoneyRateException;
import com.example.demo.exception.UserMoneyRateException;
import com.example.demo.model.RefErrors;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.RefErrorsRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository repository;
    private final ModelMapper mapper;
    private final RefErrorsRepository errorsRepository;
    private final RoleService roleService;

    @Transactional
    public User create(UserRequestDto dto) throws UserMoneyRateException, RoleMoneyRateException {
        validateOnCreate(dto);
        User user = new User();
        if (CollectionUtils.isNotEmpty(dto.getRoles())) {
            setUserRoles(dto.getRoles(), user);
        }
        mapper.map(dto, user);
        return repository.save(user);
    }
    public Page<User> list(int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        return repository.findAll(pageable);
    }

    private void validateOnCreate(UserRequestDto dto) throws UserMoneyRateException, RoleMoneyRateException {
        if (repository.findUserByLogin(dto.getLogin()).isPresent()) {
            throwException(101L);
        }
        if (repository.findUserByEmail(dto.getEmail()).isPresent()) {
            throwException(102L);
        }
        if (CollectionUtils.isNotEmpty(dto.getRoles())) {
            for (var roleTitle: dto.getRoles()) {
                if (Objects.isNull(roleService.findByTitle(roleTitle))) {
                    roleService.throwException(2L);
                }
            }
        }
    }

    @PostConstruct
    public void convert() {
        Converter<UserRequestDto, User> toEntity = mappingContext -> {
            User user = mappingContext.getDestination();
            UserRequestDto dto = mappingContext.getSource();
            user.setId(UUID.randomUUID().toString());
            user.setDateOfRegistration(new Date());
            return user;
        };
        mapper.createTypeMap(UserRequestDto.class, User.class).setPostConverter(toEntity);
    }

    public User findByName(String login) throws UserMoneyRateException {
        User user = repository.findUserByLogin(login).orElse(null);
        if (Objects.isNull(user)) {
            throwException(103L);
        }
        return user;
    }

    private void throwException(Long exceptionNumber) throws UserMoneyRateException {
        RefErrors error = errorsRepository.findByNumber(exceptionNumber).orElse(null);
        assert Objects.nonNull(error);
        throw new UserMoneyRateException(error.getRussian(), error.getEnglish(), error.getCode(), error.getNumber());
    }

    public User update(UserUpdateRequestDto dto, String login) throws UserMoneyRateException, RoleMoneyRateException {
        User user = findByName(login);
        if (StringUtils.isNotBlank(dto.getName())) {
            user.setName(dto.getName());
        }
        if (StringUtils.isNotBlank(dto.getSecondName())) {
            user.setSecondName(dto.getSecondName());
        }
        if (Objects.nonNull(dto.getDateOfBirth())) {
            user.setDateOfBirth(dto.getDateOfBirth());
        }
        setUserRoles(dto.getRoles(), user);
        return repository.save(user);
    }

    private void setUserRoles (Set<String> roleTitles, User user) throws RoleMoneyRateException {
        if (CollectionUtils.isNotEmpty(roleTitles)) {
            Set<Role> roles = new HashSet<>();
            for (var roleTitle: roleTitles) {
                roles.add(roleService.findByTitle(roleTitle));
            }
            user.setRoles(roles);
        }
    }
}

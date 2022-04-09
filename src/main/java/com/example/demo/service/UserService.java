package com.example.demo.service;

import com.example.demo.dto.request.UserRequestDto;
import com.example.demo.dto.request.UserUpdateRequestDto;
import com.example.demo.dto.response.RoleResponseDto;
import com.example.demo.dto.response.UserResponseDto;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {
    private final UserRepository repository;
    private final ModelMapper mapper;
    private final RefErrorsRepository errorsRepository;
    private final RoleService roleService;
    private final BCryptPasswordEncoder passwordEncoder;

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
    public List<User> list() {
        return repository.findAll();
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
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
            return user;
        };
        Converter<User, UserResponseDto> fromEntity = mappingContext -> {
            UserResponseDto response = mappingContext.getDestination();
            User user = mappingContext.getSource();
            response.setRoles(user.getRoles().stream()
                    .map(role -> mapper.map(role, RoleResponseDto.class))
                    .collect(Collectors.toSet())
            );
            return response;
        };
        mapper.createTypeMap(UserRequestDto.class, User.class).setPostConverter(toEntity);
        mapper.createTypeMap(User.class, UserResponseDto.class).setPostConverter(fromEntity);
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

    @Transactional
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

    @Override
    public UserDetails loadUserByUsername(String username) {
       User user = repository.findUserByLogin(username).orElse(null);
       if (Objects.isNull(user)) {
           try {
               throwException(103L);
           } catch (UserMoneyRateException e) {
               throw new RuntimeException(e);
           }
       }
        Collection<? extends GrantedAuthority> authorities =
                user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getTitle())).collect(Collectors.toSet());
        return new org.springframework.security.core.userdetails.User(user.getLogin(), user.getPassword(), authorities);
    }
}

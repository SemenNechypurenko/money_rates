package com.service;

import com.dto.request.UserRequestDto;
import com.dto.request.UserUpdateRequestDto;
import com.dto.response.RoleResponseDto;
import com.dto.response.SubscriptionResponseDto;
import com.dto.response.UserResponseDto;
import com.exception.RoleMoneyRateException;
import com.exception.UserMoneyRateException;
import com.model.RefErrors;
import com.model.Role;
import com.model.Subscription;
import com.model.User;
import com.repository.RefErrorsRepository;
import com.repository.SubscriptionRepository;
import com.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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
    private final SubscriptionRepository subscriptionRepository;
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
            Set<Subscription> subscriptions = subscriptionRepository.findAllByUser(user);
                if (CollectionUtils.isNotEmpty(subscriptions)) {
                    response.setSubscriptions(
                            subscriptions.stream().map(subscription ->
                                    mapper.map(subscription, SubscriptionResponseDto.class))
                                    .collect(Collectors.toSet())
                    );
                }
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

    public void throwException(Long exceptionNumber) throws UserMoneyRateException {
        RefErrors error = errorsRepository.findByNumber(exceptionNumber).orElse(null);
        assert Objects.nonNull(error);
        throw new UserMoneyRateException(error.getRussian(), error.getEnglish(), error.getCode(), error.getNumber());
    }

    @Transactional
    public User update(UserUpdateRequestDto dto, String login) throws UserMoneyRateException, RoleMoneyRateException {
        User user = findByName(login);
        // Проверка, что апдейтить юзера может только админ или же пользовать сам себя
        User currentUserName = findByName(getCurrentUserName());
        if (login.equals(getCurrentUserName()) ||
                currentUserName.getRoles().stream().anyMatch(role -> role.getTitle().equals("ROLE_ADMIN"))) {
            if (StringUtils.isNotBlank(dto.getName())) {
                user.setName(dto.getName());
            }
            if (StringUtils.isNotBlank(dto.getSecondName())) {
                user.setSecondName(dto.getSecondName());
            }
            if (Objects.nonNull(dto.getDateOfBirth())) {
                user.setDateOfBirth(dto.getDateOfBirth());
            }
            // Only admins can assign roles to users
            if (currentUserName.getRoles().stream().anyMatch(role -> role.getTitle().equals("ROLE_ADMIN"))) {
                setUserRoles(dto.getRoles(), user);
            }
            return repository.save(user);
        }
        throwException(104L);
        return null;
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

    public String getCurrentUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName(); 
    }

    public User findById(String id) throws UserMoneyRateException {
        User user = repository.findById(id).orElse(null);
        if (Objects.isNull(user)) {
            throwException(105L);
        }
        return user;
    }

}

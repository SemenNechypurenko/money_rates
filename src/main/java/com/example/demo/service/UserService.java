package com.example.demo.service;

import com.example.demo.dto.request.UserRequestDto;
import com.example.demo.exception.RoleMoneyRateException;
import com.example.demo.exception.UserMoneyRateException;
import com.example.demo.model.RefErrors;
import com.example.demo.model.User;
import com.example.demo.repository.RefErrorsRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final ModelMapper mapper;
    private final RefErrorsRepository errorsRepository;

    @Transactional
    public User create(UserRequestDto dto) throws UserMoneyRateException {
        validateOnCreateOrUpdate(dto);
        User user = new User();
        mapper.map(dto, user);
        return repository.save(user);
    }

    private void validateOnCreateOrUpdate(UserRequestDto dto) throws UserMoneyRateException {
        if (CollectionUtils.isNotEmpty(repository.findUsersByLogin(dto.getLogin()))) {
            RefErrors error = errorsRepository.findByNumber(101L).orElse(null);
            assert Objects.nonNull(error);
            throw new UserMoneyRateException(error.getRussian(), error.getEnglish(), error.getCode(), error.getNumber());
        }
        if (CollectionUtils.isNotEmpty(repository.findUsersByEmail(dto.getEmail()))) {
            RefErrors error = errorsRepository.findByNumber(102L).orElse(null);
            assert Objects.nonNull(error);
            throw new UserMoneyRateException(error.getRussian(), error.getEnglish(), error.getCode(), error.getNumber());
        }
    }

    @PostConstruct
    public void convert() {
        Converter<UserRequestDto, User> toEntity = mappingContext -> {
            User user = mappingContext.getDestination();
            user.setId(UUID.randomUUID().toString());
            user.setDateOfRegistration(new Date());
            return user;
        };
        mapper.createTypeMap(UserRequestDto.class, User.class).setPostConverter(toEntity);
    }
}

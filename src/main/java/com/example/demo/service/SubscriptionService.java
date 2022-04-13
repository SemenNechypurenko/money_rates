package com.example.demo.service;

import com.example.demo.dto.request.SubscriptionRequestDto;
import com.example.demo.exception.UserMoneyRateException;
import com.example.demo.model.RefErrors;
import com.example.demo.model.Subscription;
import com.example.demo.model.User;
import com.example.demo.model.enums.Timeline;
import com.example.demo.repository.RefErrorsRepository;
import com.example.demo.repository.SubscriptionRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
@RequiredArgsConstructor
@RestController
public class SubscriptionService {
    private final SubscriptionRepository repository;
    private final ModelMapper mapper;
    private final UserRepository userRepository;
    private final UserService userService;
    private final RefErrorsRepository errorsRepository;

    @Transactional
    public Subscription create(SubscriptionRequestDto dto) throws UserMoneyRateException {
        validateOnCreate(dto);
        Subscription subscription = repository.
                findByUserAndCurrency(userService.findByName(dto.getLogin()), dto.getCurrency()).orElse(null);
        if (Objects.nonNull(subscription)) {
            repository.delete(subscription);
        }
        return repository.save(mapper.map(dto, Subscription.class));
    }

    private void validateOnCreate(SubscriptionRequestDto dto) throws UserMoneyRateException {
        userService.findByName(dto.getLogin());
        if (!EnumUtils.isValidEnumIgnoreCase(Timeline.class, dto.getTimeline())) {
            // Такая временная шакала отсутствует
            // Добавить исключение
            throwException(201L);
        }
    }

    private void throwException(Long exceptionNumber) throws UserMoneyRateException {
        RefErrors error = errorsRepository.findByNumber(exceptionNumber).orElse(null);
        assert Objects.nonNull(error);
        throw new UserMoneyRateException(error.getRussian(), error.getEnglish(), error.getCode(), error.getNumber());
    }

    @PostConstruct
    public void convert() {
        Converter<SubscriptionRequestDto, Subscription> toEntity = mappingContext -> {
            Subscription subscription = mappingContext.getDestination();
            SubscriptionRequestDto dto = mappingContext.getSource();
            subscription.setId(UUID.randomUUID().toString());
            subscription.setUser(userRepository.findUserByLogin(dto.getLogin()).orElse(null));
            subscription.setDateOfSubscription(new Date());
            subscription.setTimeline(Enum.valueOf(Timeline.class, dto.getTimeline()));
            return subscription;
        };
        mapper.createTypeMap(SubscriptionRequestDto.class, Subscription.class).setPostConverter(toEntity);
    }

    public List<Subscription> list() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Set<Subscription> findByName(String login) throws UserMoneyRateException {
        User user = userRepository.findUserByLogin(login).orElse(null);
        if (Objects.isNull(user)) {
            userService.throwException(103L);
        }
        return repository.findAllByUser(user);
    }
}

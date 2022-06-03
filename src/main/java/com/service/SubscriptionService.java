package com.service;

import com.dto.request.SubscriptionRequestDto;
import com.dto.response.SubscriptionResponseDto;
import com.exception.UserMoneyRateException;
import com.model.RefErrors;
import com.model.Subscription;
import com.model.User;
import com.model.enums.Timeline;
import com.repository.RefErrorsRepository;
import com.repository.SubscriptionRepository;
import com.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    @CacheEvict(value = "subscriptions", allEntries=true)
    public Subscription create(SubscriptionRequestDto dto) throws UserMoneyRateException {
        validateOnCreate(dto);
        Subscription subscription = repository.
                findByUserAndCurrency(userService.findByName(userService.getCurrentUserName()),
                        dto.getCurrency()).orElse(null);
        if (Objects.nonNull(subscription)) {
            repository.delete(subscription);
        }
        return repository.save(mapper.map(dto, Subscription.class));
    }

    @Transactional
    @CacheEvict(value = "subscriptions", allEntries=true)
    public Subscription update (Subscription subscription) {
        return repository.save(subscription);
    }

    private void validateOnCreate(SubscriptionRequestDto dto) throws UserMoneyRateException {
        if (!EnumUtils.isValidEnumIgnoreCase(Timeline.class, dto.getTimeline())) {
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
            subscription.setUser(userRepository.findUserByLogin(userService.getCurrentUserName()).orElse(null));
            subscription.setUserId(userService.getCurrentUser().getId());
            subscription.setDateOfSubscription(new Date());
            subscription.setTimeline(Enum.valueOf(Timeline.class, dto.getTimeline()));
            return subscription;
        };
        Converter<Subscription, SubscriptionResponseDto> fromEntity = MappingContext::getDestination;

        mapper.createTypeMap(SubscriptionRequestDto.class, Subscription.class).setPostConverter(toEntity);
        mapper.createTypeMap(Subscription.class, SubscriptionResponseDto.class).setPostConverter(fromEntity);
    }

    @Cacheable("subscriptions")
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

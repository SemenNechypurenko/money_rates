package com.example.demo.service;

import com.example.demo.dto.request.SubscriptionRequestDto;
import com.example.demo.exception.UserMoneyRateException;
import com.example.demo.model.RefErrors;
import com.example.demo.model.Subscription;
import com.example.demo.model.enums.Timeline;
import com.example.demo.repository.RefErrorsRepository;
import com.example.demo.repository.SubscriptionRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@RestController
public class SubscriptionService {
    private final SubscriptionRepository repository;
    private final ModelMapper mapper;
    private final UserRepository userRepository;
    private final UserService userService;
    private final RefErrorsRepository errorsRepository;

    public Subscription create(SubscriptionRequestDto dto) throws UserMoneyRateException {
        validateOnCreate(dto);
        Subscription subscription = mapper.map(dto, Subscription.class);
        return repository.save(subscription);
    }

    private void validateOnCreate(SubscriptionRequestDto dto) throws UserMoneyRateException {
        userService.findByName(dto.getLogin());
        if (!EnumUtils.isValidEnumIgnoreCase(Timeline.class, dto.getTimeline())) {
            // Такая временная шакала отсутствует
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

}

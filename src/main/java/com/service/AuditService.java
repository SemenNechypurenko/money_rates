package com.service;

import com.model.Audit;
import com.repository.AuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {
    private final AuditRepository repository;
    private final UserService userService;

    @Transactional
    public Audit save(Audit audit) {
        return repository.save(audit);
    }
}

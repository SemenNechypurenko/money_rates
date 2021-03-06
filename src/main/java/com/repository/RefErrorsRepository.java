package com.repository;

import com.model.RefErrors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefErrorsRepository extends JpaRepository<RefErrors, String> {
    Optional<RefErrors> findByNumber(Long number);
}

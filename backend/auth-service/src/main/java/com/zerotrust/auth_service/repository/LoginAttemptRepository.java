package com.zerotrust.auth_service.repository;

import com.zerotrust.auth_service.entity.LoginAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, Long> {
}

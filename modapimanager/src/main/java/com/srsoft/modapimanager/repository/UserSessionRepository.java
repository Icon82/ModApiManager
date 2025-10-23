package com.srsoft.modapimanager.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.srsoft.modapimanager.entity.UserSession;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    Optional<UserSession> findByUserId(Long userId);
    Optional<UserSession> findByToken(String token);
    void deleteByUserId(Long userId);
}

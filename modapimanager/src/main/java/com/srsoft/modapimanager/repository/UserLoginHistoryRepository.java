package com.srsoft.modapimanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.srsoft.modapimanager.entity.User;
import com.srsoft.modapimanager.entity.UserLoginHistory;
import com.srsoft.modapimanager.entity.UserLoginHistory.LoginStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserLoginHistoryRepository extends JpaRepository<UserLoginHistory, Long> {
    Optional<UserLoginHistory> findTopByUserOrderByLoginTimestampDesc(User user);
    
    List<UserLoginHistory> findByUserOrderByLoginTimestampDesc(User user);
    
    long countByUserAndStatusAndLoginTimestampAfter(User user, LoginStatus status, LocalDateTime after);
    
    List<UserLoginHistory> findByStatusAndLoginTimestampAfter(LoginStatus status, LocalDateTime after);
    
    List<UserLoginHistory> findByUserAndStatusOrderByLoginTimestampDesc(User user, LoginStatus status);
}

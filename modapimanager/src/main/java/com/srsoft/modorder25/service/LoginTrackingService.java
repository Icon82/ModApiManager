package com.srsoft.modorder25.service;

import com.srsoft.modorder25.entity.User;
import com.srsoft.modorder25.entity.UserLoginHistory;
import com.srsoft.modorder25.entity.UserLoginHistory.LoginStatus;
import com.srsoft.modorder25.repository.UserLoginHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class LoginTrackingService {

    private final UserLoginHistoryRepository loginHistoryRepository;
    private final HttpServletRequest request;

    public void trackSuccessfulLogin(User user) {
        trackLogin(user, LoginStatus.SUCCESS, null);
    }

    public void trackFailedLogin(User user, String reason) {
        trackLogin(user, LoginStatus.FAILURE, reason);
    }

    private void trackLogin(User user, LoginStatus status, String failureReason) {
        UserLoginHistory loginHistory = new UserLoginHistory();
        loginHistory.setUser(user);
        loginHistory.setLoginTimestamp(LocalDateTime.now());
        loginHistory.setIpAddress(getClientIp());
        loginHistory.setUserAgent(request.getHeader("User-Agent"));
        loginHistory.setStatus(status);
        loginHistory.setFailureReason(failureReason);

        loginHistoryRepository.save(loginHistory);
    }

    private String getClientIp() {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    public UserLoginHistory getLastLoginAttempt(User user) {
        return loginHistoryRepository.findTopByUserOrderByLoginTimestampDesc(user)
            .orElse(null);
    }

    public List<UserLoginHistory> getLoginHistory(User user) {
        return loginHistoryRepository.findByUserOrderByLoginTimestampDesc(user);
    }

    public boolean hasRecentFailedAttempts(User user, int maxAttempts, int minutesWindow) {
        LocalDateTime windowStart = LocalDateTime.now().minusMinutes(minutesWindow);
        long failedAttempts = loginHistoryRepository
            .countByUserAndStatusAndLoginTimestampAfter(user, LoginStatus.FAILURE, windowStart);
        return failedAttempts >= maxAttempts;
    }

    public List<UserLoginHistory> getRecentFailedAttempts() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
        return loginHistoryRepository.findByStatusAndLoginTimestampAfter(LoginStatus.FAILURE, cutoff);
    }

    public List<UserLoginHistory> getUserFailedAttempts(User user) {
        return loginHistoryRepository.findByUserAndStatusOrderByLoginTimestampDesc(user, LoginStatus.FAILURE);
    }
}
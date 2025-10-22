package com.srsoft.modorder25.controller;

import com.srsoft.modorder25.entity.UserLoginHistory;
import com.srsoft.modorder25.service.LoginTrackingService;
import com.srsoft.modorder25.service.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/login-history")
@RequiredArgsConstructor
@Tag(name = "09.Info Track Login ", description = "API per la registrazione del Login Utente ")
public class LoginHistoryController {
    private final LoginTrackingService loginTrackingService;
    private final UserService userService;

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<List<UserLoginHistory>> getUserLoginHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(loginTrackingService.getLoginHistory(userService.findById(userId)));
    }

    @GetMapping("/failed-attempts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserLoginHistory>> getFailedAttempts() {
        return ResponseEntity.ok(loginTrackingService.getRecentFailedAttempts());
    }

    @GetMapping("/user/{userId}/failed")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<List<UserLoginHistory>> getUserFailedAttempts(@PathVariable Long userId) {
        return ResponseEntity.ok(loginTrackingService.getUserFailedAttempts(userService.findById(userId)));
    }
}
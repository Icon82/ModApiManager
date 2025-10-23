package com.srsoft.modapimanager.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_login_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime loginTimestamp;

    private String ipAddress;
    
    private String userAgent;
    
    @Enumerated(EnumType.STRING)
    private LoginStatus status;
    
    private String failureReason;

    public enum LoginStatus {
        SUCCESS, FAILURE
    }
}
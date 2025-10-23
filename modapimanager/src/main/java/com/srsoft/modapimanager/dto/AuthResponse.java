package com.srsoft.modapimanager.dto;


import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.srsoft.modapimanager.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private Set<Role> roles = new HashSet<>();
    private Date expireDate;
    private int permessi; 
    }
package com.srsoft.modapimanager.dto;

import jakarta.validation.constraints.NotBlank;

import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class SettingAppRequest {
	@NotBlank(message = "La Chiave è obbligatorio")
    private String chiave;
	@NotBlank(message = "Il valore è obbligatorio")
    private String valore;
    private boolean  encrypted ; 
    private String description;
    private String category;
}
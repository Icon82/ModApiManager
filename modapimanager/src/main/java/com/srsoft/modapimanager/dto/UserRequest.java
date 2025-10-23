package com.srsoft.modapimanager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequest {
    @NotBlank(message = "Username è obbligatorio")
    @Size(min = 3, max = 50, message = "Username deve essere tra 3 e 50 caratteri")
    private String username;

    @NotBlank(message = "Password è obbligatoria")
    @Size(min = 6, message = "Password deve essere di almeno 6 caratteri")
    private String password;

    @NotBlank(message = "Email è obbligatoria")
    @Email(message = "Email non valida")
    private String email;
    
    private int permessi;    
    
}

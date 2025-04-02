package com.srsoft.modorder25.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordChangeRequest {
    @NotBlank(message = "La vecchia password è obbligatoria")
    private String oldPassword;

    @NotBlank(message = "La nuova password è obbligatoria")
    @Size(min = 6, message = "La password deve essere di almeno 6 caratteri")
    private String newPassword;

}

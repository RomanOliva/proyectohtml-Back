package com.example.ms_warframe.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {

    @NotBlank(message = "Debe ingresar el correo")
    private String correo;

    @NotBlank(message = "Debe ingresar la contraseña")
    private String password;
}

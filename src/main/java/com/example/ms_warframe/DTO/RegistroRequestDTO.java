package com.example.ms_warframe.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistroRequestDTO {

    @NotBlank(message = "Debe ingresar el correo")
    @Email(message = "El correo no tiene un formato válido")
    @Size(max = 120, message = "El correo no puede superar los 120 caracteres")
    private String correo;

    @NotBlank(message = "Debe ingresar la contraseña")
    @Size(min = 6, max = 72, message = "La contraseña debe tener entre 6 y 72 caracteres")
    private String password;
}

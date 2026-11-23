package com.example.ms_warframe.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthDTO {
    private String token;
    private String correo;
    private String rol;
}

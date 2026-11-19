package com.example.ms_warframe.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoriaRequestDTO {

    @NotBlank(message = "El nombre de la categoría no puede estar vacío")
    @Size(max = 30, message = "El nombre no puede superar los 30 caracteres")
    private String nombre;
}

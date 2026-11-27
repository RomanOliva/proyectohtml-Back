package com.example.ms_warframe.DTO;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ItemRequestDTO {

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 60, message = "El nombre no puede superar los 60 caracteres")
    private String nombre;

    @NotNull(message = "Debe indicar la categoría")
    private Integer idCategoria;

    @NotBlank(message = "Debe indicar el motivo de poco uso")
    @Size(max = 150, message = "El motivo no puede superar los 150 caracteres")
    private String motivoPocoUso;

    @NotNull(message = "Debe indicar el precio")
    @Min(value = 1, message = "El precio debe ser mayor a 0")
    @Max(value = 3000, message = "El precio no puede superar los 3000 créditos")
    private Integer precioCreditos;

    @Min(value = 0, message = "El stock no puede ser negativo")
    @Max(value = 100000, message = "El stock no puede superar las 100000 unidades")
    private Integer stock;

    @Size(max = 255, message = "La URL de la imagen no puede superar los 255 caracteres")
    private String imagenUrl;
}

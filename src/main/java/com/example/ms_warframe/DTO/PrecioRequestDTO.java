package com.example.ms_warframe.DTO;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PrecioRequestDTO {

    @NotNull(message = "Debe indicar el precio")
    @Min(value = 1, message = "El precio debe ser mayor a 0")
    @Max(value = 3000, message = "El precio no puede superar los 3000 créditos")
    private Integer precioCreditos;
}

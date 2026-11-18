package com.example.ms_warframe.DTO;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AjusteMasivoRequestDTO {

    @NotNull(message = "Debe indicar el precio mínimo del umbral")
    private Integer precioMin;

    @NotNull(message = "Debe indicar el precio máximo del umbral")
    private Integer precioMax;

    @NotNull(message = "Debe indicar el porcentaje")
    @DecimalMin(value = "-100", inclusive = false, message = "El porcentaje debe ser mayor a -100")
    private BigDecimal porcentaje;
}

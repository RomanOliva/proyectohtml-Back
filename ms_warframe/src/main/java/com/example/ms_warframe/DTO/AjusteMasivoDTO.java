package com.example.ms_warframe.DTO;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AjusteMasivoDTO {
    private Integer precioMin;
    private Integer precioMax;
    private BigDecimal porcentaje;
    private int itemsAfectados;
}

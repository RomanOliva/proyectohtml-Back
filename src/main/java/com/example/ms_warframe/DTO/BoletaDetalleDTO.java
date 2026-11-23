package com.example.ms_warframe.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BoletaDetalleDTO {
    private Integer productoId;
    private String nombre;
    private Integer cantidad;
    private Integer precioUnitario;
    private Integer subtotal;
}

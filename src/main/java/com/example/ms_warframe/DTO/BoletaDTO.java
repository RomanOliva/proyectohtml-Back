package com.example.ms_warframe.DTO;

import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BoletaDTO {
    private Integer id;
    private String correo;
    private Instant fecha;
    private Integer total;
    private List<BoletaDetalleDTO> detalles;
}

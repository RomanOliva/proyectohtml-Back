package com.example.ms_warframe.DTO;

import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorDTO {
    private int status;
    private String mensaje;
    private List<String> detalles;
    private Instant fecha;
}

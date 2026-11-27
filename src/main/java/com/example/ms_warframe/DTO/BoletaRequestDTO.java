package com.example.ms_warframe.DTO;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BoletaRequestDTO {

    @Valid
    @NotEmpty(message = "El carrito está vacío")
    @Size(max = 100, message = "Una boleta no puede tener más de 100 productos distintos")
    private List<DetalleRequestDTO> detalles;
}

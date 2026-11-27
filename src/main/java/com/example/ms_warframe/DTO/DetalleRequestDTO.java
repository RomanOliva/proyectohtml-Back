package com.example.ms_warframe.DTO;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DetalleRequestDTO {

    @NotNull(message = "Debe indicar el producto")
    private Integer productoId;

    @NotNull(message = "Debe indicar la cantidad")
    @Min(value = 1, message = "La cantidad mínima es 1")
    @Max(value = 100000, message = "La cantidad no puede superar las 100000 unidades")
    private Integer cantidad;
}

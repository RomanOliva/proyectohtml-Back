package com.example.ms_warframe.DTO;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductoRequestDTO {

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 60, message = "El nombre no puede superar los 60 caracteres")
    private String nombre;

    @NotBlank(message = "La descripción no puede estar vacía")
    @Size(max = 150, message = "La descripción no puede superar los 150 caracteres")
    private String descripcion;

    @NotNull(message = "Debe indicar el precio")
    @Min(value = 1, message = "El precio debe ser mayor a 0")
    @Max(value = 3000, message = "El precio no puede superar los 3000")
    private Integer precio;

    @NotNull(message = "Debe indicar el stock")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Max(value = 100000, message = "El stock no puede superar las 100000 unidades")
    private Integer stock;

    @Size(max = 255, message = "La URL de la imagen no puede superar los 255 caracteres")
    private String imagen;

    private Integer categoriaId;

    private CategoriaRefDTO categoria;

    @Data
    public static class CategoriaRefDTO {
        private Integer id;
    }

    public Integer idCategoriaEfectivo() {
        if (categoriaId != null) {
            return categoriaId;
        }
        return categoria != null ? categoria.getId() : null;
    }
}

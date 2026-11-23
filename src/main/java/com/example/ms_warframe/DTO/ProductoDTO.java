package com.example.ms_warframe.DTO;

import lombok.Data;

@Data
public class ProductoDTO {
    private Integer id;
    private String nombre;
    private String descripcion;
    private Integer precio;
    private Integer stock;
    private String imagen;
    private CategoriaResumenDTO categoria;
}

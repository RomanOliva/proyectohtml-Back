package com.example.ms_warframe.DTO;

import org.springframework.hateoas.RepresentationModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ItemDTO extends RepresentationModel<ItemDTO> {
    private Integer id;
    private String nombre;
    private Integer idCategoria;
    private String categoriaNombre;
    private String motivoPocoUso;
    private Integer precioCreditos;
    private String clasificacion;
    private String imagenUrl;
}

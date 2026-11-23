package com.example.ms_warframe.DTO;

import org.springframework.hateoas.RepresentationModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class CategoriaDTO extends RepresentationModel<CategoriaDTO> {
    private Integer id;
    private String nombre;
}

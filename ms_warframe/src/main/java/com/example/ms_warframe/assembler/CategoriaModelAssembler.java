package com.example.ms_warframe.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.example.ms_warframe.DTO.CategoriaDTO;
import com.example.ms_warframe.controller.CategoriaController;
import com.example.ms_warframe.model.Categoria;

@Component
public class CategoriaModelAssembler implements RepresentationModelAssembler<Categoria, CategoriaDTO> {

    @Override
    public CategoriaDTO toModel(Categoria categoria) {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setId(categoria.getId());
        dto.setNombre(categoria.getNombre());

        dto.add(linkTo(methodOn(CategoriaController.class).obtener(categoria.getId())).withSelfRel());
        dto.add(linkTo(methodOn(CategoriaController.class).listar()).withRel("categorias"));
        return dto;
    }

    @Override
    public CollectionModel<CategoriaDTO> toCollectionModel(Iterable<? extends Categoria> entities) {
        CollectionModel<CategoriaDTO> coleccion = RepresentationModelAssembler.super.toCollectionModel(entities);
        coleccion.add(linkTo(methodOn(CategoriaController.class).listar()).withSelfRel());
        return coleccion;
    }
}

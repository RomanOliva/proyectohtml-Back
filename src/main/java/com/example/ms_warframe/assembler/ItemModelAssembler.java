package com.example.ms_warframe.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.example.ms_warframe.DTO.ItemDTO;
import com.example.ms_warframe.controller.ItemController;
import com.example.ms_warframe.model.ClasificacionPrecio;
import com.example.ms_warframe.model.Item;

@Component
public class ItemModelAssembler implements RepresentationModelAssembler<Item, ItemDTO> {

    @Override
    public ItemDTO toModel(Item item) {
        ItemDTO dto = new ItemDTO();
        dto.setId(item.getId());
        dto.setNombre(item.getNombre());
        dto.setIdCategoria(item.getCategoria().getId());
        dto.setCategoriaNombre(item.getCategoria().getNombre());
        dto.setMotivoPocoUso(item.getMotivoPocoUso());
        dto.setPrecioCreditos(item.getPrecioCreditos());
        dto.setClasificacion(ClasificacionPrecio.de(item.getPrecioCreditos()).name());
        dto.setStock(item.getStock());
        dto.setImagenUrl(item.getImagenUrl());

        dto.add(linkTo(methodOn(ItemController.class).obtener(item.getId())).withSelfRel());
        dto.add(linkTo(methodOn(ItemController.class).listar(null, null, null, null)).withRel("items"));
        return dto;
    }

    @Override
    public CollectionModel<ItemDTO> toCollectionModel(Iterable<? extends Item> entities) {
        CollectionModel<ItemDTO> coleccion = RepresentationModelAssembler.super.toCollectionModel(entities);
        coleccion.add(linkTo(methodOn(ItemController.class).listar(null, null, null, null)).withSelfRel());
        return coleccion;
    }
}

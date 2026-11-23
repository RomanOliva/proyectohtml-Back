package com.example.ms_warframe.assembler;

import org.springframework.stereotype.Component;

import com.example.ms_warframe.DTO.CategoriaResumenDTO;
import com.example.ms_warframe.DTO.ItemRequestDTO;
import com.example.ms_warframe.DTO.ProductoDTO;
import com.example.ms_warframe.DTO.ProductoRequestDTO;
import com.example.ms_warframe.model.Item;

@Component
public class ProductoAssembler {

    public ProductoDTO toDTO(Item item) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(item.getId());
        dto.setNombre(item.getNombre());
        dto.setDescripcion(item.getMotivoPocoUso());
        dto.setPrecio(item.getPrecioCreditos());
        dto.setStock(item.getStock());
        dto.setImagen(item.getImagenUrl());
        dto.setCategoria(new CategoriaResumenDTO(
                item.getCategoria().getId(), item.getCategoria().getNombre()));
        return dto;
    }

    public ItemRequestDTO toItemRequest(ProductoRequestDTO producto) {
        ItemRequestDTO request = new ItemRequestDTO();
        request.setNombre(producto.getNombre());
        request.setIdCategoria(producto.idCategoriaEfectivo());
        request.setMotivoPocoUso(producto.getDescripcion());
        request.setPrecioCreditos(producto.getPrecio());
        request.setStock(producto.getStock());

        String imagen = producto.getImagen() == null ? null : producto.getImagen().trim();
        request.setImagenUrl(imagen == null || imagen.isEmpty() ? null : imagen);
        return request;
    }
}

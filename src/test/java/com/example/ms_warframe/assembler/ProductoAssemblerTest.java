package com.example.ms_warframe.assembler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.example.ms_warframe.DTO.ItemRequestDTO;
import com.example.ms_warframe.DTO.ProductoDTO;
import com.example.ms_warframe.DTO.ProductoRequestDTO;
import com.example.ms_warframe.model.Categoria;
import com.example.ms_warframe.model.Item;

class ProductoAssemblerTest {

    private final ProductoAssembler assembler = new ProductoAssembler();

    @Test
    void itemSeTraduceAVocabularioDelFront() {
        Categoria c = new Categoria();
        c.setId(3);
        c.setNombre("Mods");
        Item item = new Item();
        item.setId(7);
        item.setNombre("Serration");
        item.setMotivoPocoUso("Reemplazado por otro mod");
        item.setPrecioCreditos(650);
        item.setStock(12);
        item.setImagenUrl("https://x/y.png");
        item.setCategoria(c);

        ProductoDTO dto = assembler.toDTO(item);

        assertEquals("Reemplazado por otro mod", dto.getDescripcion());
        assertEquals(650, dto.getPrecio());
        assertEquals(12, dto.getStock());
        assertEquals("https://x/y.png", dto.getImagen());
        assertEquals(3, dto.getCategoria().getId());
        assertEquals("Mods", dto.getCategoria().getNombre());
    }

    @Test
    void requestAceptaCategoriaIdOCategoriaAnidadaYImagenVaciaPasaANull() {
        ProductoRequestDTO req = new ProductoRequestDTO();
        req.setNombre("X");
        req.setDescripcion("d");
        req.setPrecio(10);
        req.setStock(1);
        req.setImagen("   ");
        ProductoRequestDTO.CategoriaRefDTO ref = new ProductoRequestDTO.CategoriaRefDTO();
        ref.setId(2);
        req.setCategoria(ref);

        ItemRequestDTO item = assembler.toItemRequest(req);

        assertEquals(2, item.getIdCategoria());
        assertNull(item.getImagenUrl());
        assertEquals("d", item.getMotivoPocoUso());
    }
}

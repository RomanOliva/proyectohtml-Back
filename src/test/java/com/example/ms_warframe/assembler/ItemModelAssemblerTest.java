package com.example.ms_warframe.assembler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.ms_warframe.DTO.ItemDTO;
import com.example.ms_warframe.model.Categoria;
import com.example.ms_warframe.model.ClasificacionPrecio;
import com.example.ms_warframe.model.Item;

class ItemModelAssemblerTest {

    private final ItemModelAssembler assembler = new ItemModelAssembler();

    private Item itemConPrecio(int precio) {
        Categoria categoria = new Categoria();
        ReflectionTestUtils.setField(categoria, "id", 1);
        ReflectionTestUtils.setField(categoria, "nombre", "Mod Inutil");

        Item item = new Item();
        item.setId(7);
        item.setNombre("Looter");
        item.setCategoria(categoria);
        item.setMotivoPocoUso("Nadie lo usa");
        item.setPrecioCreditos(precio);
        return item;
    }

    @Test
    void toModel_copiaLosDatosYAgregaLosEnlaces() {
        ItemDTO dto = assembler.toModel(itemConPrecio(350));

        assertEquals(7, dto.getId());
        assertEquals("Looter", dto.getNombre());
        assertEquals("Mod Inutil", dto.getCategoriaNombre());
        assertTrue(dto.getLink(IanaLinkRelations.SELF).isPresent());
        assertTrue(dto.getLink("items").isPresent());
    }

    @Test
    void clasificacion_siguelaMismaReglaQueLaBaseDeDatos() {
        assertEquals(ClasificacionPrecio.BAJO, ClasificacionPrecio.de(799));
        assertEquals(ClasificacionPrecio.INTERMEDIO, ClasificacionPrecio.de(800));
        assertEquals(ClasificacionPrecio.INTERMEDIO, ClasificacionPrecio.de(1500));
        assertEquals(ClasificacionPrecio.ALTO, ClasificacionPrecio.de(1501));
        assertEquals("ALTO", assembler.toModel(itemConPrecio(2500)).getClasificacion());
    }
}

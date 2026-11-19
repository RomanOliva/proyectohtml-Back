package com.example.ms_warframe.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.ms_warframe.DTO.AjusteMasivoDTO;
import com.example.ms_warframe.DTO.AjusteMasivoRequestDTO;
import com.example.ms_warframe.DTO.ItemRequestDTO;
import com.example.ms_warframe.exception.ConflictoException;
import com.example.ms_warframe.exception.RecursoNoEncontradoException;
import com.example.ms_warframe.exception.ReglaNegocioException;
import com.example.ms_warframe.model.Categoria;
import com.example.ms_warframe.model.Item;
import com.example.ms_warframe.repository.CategoriaRepository;
import com.example.ms_warframe.repository.ItemRepository;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private ItemService itemService;

    private Categoria categoria(int id, String nombre) {
        Categoria c = new Categoria();
        ReflectionTestUtils.setField(c, "id", id);
        ReflectionTestUtils.setField(c, "nombre", nombre);
        return c;
    }

    private Item item(int id, int precio) {
        Item i = new Item();
        i.setId(id);
        i.setNombre("Item " + id);
        i.setPrecioCreditos(precio);
        return i;
    }

    private ItemRequestDTO request(String nombre, Integer idCategoria, Integer precio) {
        ItemRequestDTO dto = new ItemRequestDTO();
        dto.setNombre(nombre);
        dto.setIdCategoria(idCategoria);
        dto.setMotivoPocoUso("Nadie lo usa");
        dto.setPrecioCreditos(precio);
        return dto;
    }

    @Test
    void crear_guardaElItemConSuCategoria() {
        Categoria mods = categoria(1, "Mod Inutil");
        when(itemRepository.existsByNombreIgnoreCase("Looter")).thenReturn(false);
        when(categoriaRepository.findById(1)).thenReturn(Optional.of(mods));
        when(itemRepository.save(any(Item.class))).thenAnswer(inv -> inv.getArgument(0));

        Item resultado = itemService.crear(request("Looter", 1, 350));

        assertEquals("Looter", resultado.getNombre());
        assertEquals(mods, resultado.getCategoria());
        assertEquals(350, resultado.getPrecioCreditos());
    }

    @Test
    void crear_conCategoriaInexistente_lanzaNoEncontrado() {
        when(itemRepository.existsByNombreIgnoreCase("Looter")).thenReturn(false);
        when(categoriaRepository.findById(9)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> itemService.crear(request("Looter", 9, 350)));
        verify(itemRepository, never()).save(any());
    }

    @Test
    void crear_conNombreRepetido_lanzaConflicto() {
        when(itemRepository.existsByNombreIgnoreCase("Looter")).thenReturn(true);

        assertThrows(ConflictoException.class, () -> itemService.crear(request("Looter", 1, 350)));
    }

    @Test
    void crear_conPrecioFueraDeRango_lanzaReglaDeNegocio() {
        when(itemRepository.existsByNombreIgnoreCase("Looter")).thenReturn(false);

        assertThrows(ReglaNegocioException.class, () -> itemService.crear(request("Looter", 1, 5000)));
    }

    @Test
    void actualizarPrecio_cambiaElPrecio() {
        Item existente = item(1, 350);
        when(itemRepository.findById(1)).thenReturn(Optional.of(existente));
        when(itemRepository.save(any(Item.class))).thenAnswer(inv -> inv.getArgument(0));

        Item resultado = itemService.actualizarPrecio(1, 999);

        assertEquals(999, resultado.getPrecioCreditos());
    }

    @Test
    void listar_conUmbralInvertido_lanzaReglaDeNegocio() {
        assertThrows(ReglaNegocioException.class, () -> itemService.listar(null, 1500, 500, null));
    }

    @Test
    void ajustarPorUmbral_rebajaUnDiezPorCiento() {
        Item a = item(1, 1000);
        Item b = item(2, 500);
        when(itemRepository.filtrar(null, 500, 1500, null)).thenReturn(List.of(a, b));

        AjusteMasivoRequestDTO req = new AjusteMasivoRequestDTO();
        req.setPrecioMin(500);
        req.setPrecioMax(1500);
        req.setPorcentaje(new BigDecimal("-10"));

        AjusteMasivoDTO resultado = itemService.ajustarPorUmbral(req);

        assertEquals(2, resultado.getItemsAfectados());
        assertEquals(900, a.getPrecioCreditos());
        assertEquals(450, b.getPrecioCreditos());
    }

    @Test
    void ajustarPorUmbral_siAlgunPrecioSePasaDelMaximo_noModificaNinguno() {
        Item a = item(1, 1000);
        Item b = item(2, 2800);
        when(itemRepository.filtrar(null, 500, 3000, null)).thenReturn(List.of(a, b));

        AjusteMasivoRequestDTO req = new AjusteMasivoRequestDTO();
        req.setPrecioMin(500);
        req.setPrecioMax(3000);
        req.setPorcentaje(new BigDecimal("50"));

        assertThrows(ReglaNegocioException.class, () -> itemService.ajustarPorUmbral(req));
        assertEquals(1000, a.getPrecioCreditos());
        assertEquals(2800, b.getPrecioCreditos());
    }
}

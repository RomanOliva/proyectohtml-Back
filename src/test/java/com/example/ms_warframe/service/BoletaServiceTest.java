package com.example.ms_warframe.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.example.ms_warframe.DTO.BoletaDTO;
import com.example.ms_warframe.DTO.BoletaRequestDTO;
import com.example.ms_warframe.DTO.DetalleRequestDTO;
import com.example.ms_warframe.assembler.BoletaAssembler;
import com.example.ms_warframe.exception.ConflictoException;
import com.example.ms_warframe.exception.CredencialesInvalidasException;
import com.example.ms_warframe.exception.RecursoNoEncontradoException;
import com.example.ms_warframe.model.Boleta;
import com.example.ms_warframe.model.Item;
import com.example.ms_warframe.model.Usuario;
import com.example.ms_warframe.repository.BoletaRepository;
import com.example.ms_warframe.repository.ItemRepository;
import com.example.ms_warframe.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BoletaServiceTest {

    @Mock
    private BoletaRepository boletaRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    private BoletaService boletaService;
    private Usuario usuario;

    @BeforeEach
    void preparar() {
        boletaService = new BoletaService(boletaRepository, itemRepository, usuarioRepository, new BoletaAssembler());
        usuario = new Usuario();
        usuario.setCorreo("ana@mail.com");
        when(usuarioRepository.findByCorreo("ana@mail.com")).thenReturn(Optional.of(usuario));
        when(boletaRepository.save(any(Boleta.class))).thenAnswer(i -> {
            Boleta b = i.getArgument(0);
            b.setId(1);
            return b;
        });
    }

    private Item item(int id, String nombre, int precio, int stock) {
        Item i = new Item();
        i.setId(id);
        i.setNombre(nombre);
        i.setPrecioCreditos(precio);
        i.setStock(stock);
        return i;
    }

    private BoletaRequestDTO pedido(int... idYCantidad) {
        BoletaRequestDTO dto = new BoletaRequestDTO();
        java.util.ArrayList<DetalleRequestDTO> lista = new java.util.ArrayList<>();
        for (int k = 0; k < idYCantidad.length; k += 2) {
            DetalleRequestDTO d = new DetalleRequestDTO();
            d.setProductoId(idYCantidad[k]);
            d.setCantidad(idYCantidad[k + 1]);
            lista.add(d);
        }
        dto.setDetalles(lista);
        return dto;
    }

    @Test
    void compraDescuentaStockYCalculaTotal() {
        Item a = item(1, "A", 100, 10);
        Item b = item(2, "B", 50, 5);
        when(itemRepository.bloquearPorIds(anyCollection())).thenReturn(List.of(a, b));

        BoletaDTO boleta = boletaService.crear("ana@mail.com", pedido(1, 2, 2, 3));

        assertEquals(350, boleta.getTotal());
        assertEquals(8, a.getStock());
        assertEquals(2, b.getStock());
        assertEquals(2, boleta.getDetalles().size());
    }

    @Test
    void mismoProductoEnDosLineasSeSuma() {
        Item a = item(1, "A", 100, 10);
        when(itemRepository.bloquearPorIds(anyCollection())).thenReturn(List.of(a));

        BoletaDTO boleta = boletaService.crear("ana@mail.com", pedido(1, 2, 1, 3));

        assertEquals(500, boleta.getTotal());
        assertEquals(5, a.getStock());
        assertEquals(1, boleta.getDetalles().size());
    }

    @Test
    void stockInsuficienteLanzaConflictoYNoGuardaBoleta() {
        Item a = item(1, "Rhino", 100, 1);
        when(itemRepository.bloquearPorIds(anyCollection())).thenReturn(List.of(a));

        ConflictoException ex = assertThrows(ConflictoException.class,
                () -> boletaService.crear("ana@mail.com", pedido(1, 2)));

        assertEquals("Stock insuficiente de «Rhino»: queda 1 unidad.", ex.getMessage());
        verify(boletaRepository, never()).save(any());
    }

    @Test
    void productoInexistenteLanzaNoEncontrado() {
        when(itemRepository.bloquearPorIds(anyCollection())).thenReturn(List.of());

        assertThrows(RecursoNoEncontradoException.class,
                () -> boletaService.crear("ana@mail.com", pedido(99, 1)));
    }

    @Test
    void usuarioInexistenteLanzaCredencialesInvalidas() {
        when(usuarioRepository.findByCorreo("fantasma@mail.com")).thenReturn(Optional.empty());

        assertThrows(CredencialesInvalidasException.class,
                () -> boletaService.crear("fantasma@mail.com", pedido(1, 1)));
    }
}

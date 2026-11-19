package com.example.ms_warframe.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.ms_warframe.DTO.CategoriaRequestDTO;
import com.example.ms_warframe.exception.ConflictoException;
import com.example.ms_warframe.exception.RecursoNoEncontradoException;
import com.example.ms_warframe.model.Categoria;
import com.example.ms_warframe.repository.CategoriaRepository;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    private CategoriaRequestDTO request(String nombre) {
        CategoriaRequestDTO dto = new CategoriaRequestDTO();
        dto.setNombre(nombre);
        return dto;
    }

    @Test
    void crear_guardaLaCategoriaConElNombreSinEspacios() {
        when(categoriaRepository.existsByNombreIgnoreCase("Mod Inutil")).thenReturn(false);
        when(categoriaRepository.save(any(Categoria.class))).thenAnswer(inv -> inv.getArgument(0));

        Categoria resultado = categoriaService.crear(request("  Mod Inutil  "));

        assertEquals("Mod Inutil", resultado.getNombre());
    }

    @Test
    void crear_conNombreRepetido_lanzaConflicto() {
        when(categoriaRepository.existsByNombreIgnoreCase("Arcano")).thenReturn(true);

        assertThrows(ConflictoException.class, () -> categoriaService.crear(request("Arcano")));
        verify(categoriaRepository, never()).save(any());
    }

    @Test
    void obtener_conIdInexistente_lanzaNoEncontrado() {
        when(categoriaRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> categoriaService.obtener(99));
    }

    @Test
    void eliminar_borraYHaceFlush() {
        Categoria categoria = new Categoria();
        categoria.setId(1);
        categoria.setNombre("Mod");
        when(categoriaRepository.findById(1)).thenReturn(Optional.of(categoria));

        categoriaService.eliminar(1);

        verify(categoriaRepository).delete(categoria);
        verify(categoriaRepository).flush();
    }
}

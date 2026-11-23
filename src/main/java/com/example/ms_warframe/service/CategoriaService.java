package com.example.ms_warframe.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ms_warframe.DTO.CategoriaRequestDTO;
import com.example.ms_warframe.exception.ConflictoException;
import com.example.ms_warframe.exception.RecursoNoEncontradoException;
import com.example.ms_warframe.model.Categoria;
import com.example.ms_warframe.repository.CategoriaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    @Transactional(readOnly = true)
    public List<Categoria> listar() {
        return categoriaRepository.findAll(Sort.by("id"));
    }

    @Transactional(readOnly = true)
    public Categoria obtener(Integer id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la categoría con id " + id));
    }

    @Transactional
    public Categoria crear(CategoriaRequestDTO request) {
        String nombre = request.getNombre().trim();
        if (categoriaRepository.existsByNombreIgnoreCase(nombre)) {
            throw new ConflictoException("Ya existe una categoría llamada '" + nombre + "'");
        }
        Categoria categoria = new Categoria();
        categoria.setNombre(nombre);
        log.info("Creando categoría '{}'", nombre);
        return categoriaRepository.save(categoria);
    }

    @Transactional
    public Categoria actualizar(Integer id, CategoriaRequestDTO request) {
        Categoria categoria = obtener(id);
        String nombre = request.getNombre().trim();
        if (categoriaRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw new ConflictoException("Ya existe otra categoría llamada '" + nombre + "'");
        }
        categoria.setNombre(nombre);
        return categoriaRepository.save(categoria);
    }

    @Transactional
    public void eliminar(Integer id) {
        Categoria categoria = obtener(id);
        categoriaRepository.delete(categoria);

        categoriaRepository.flush();
        log.info("Categoría {} eliminada", id);
    }
}

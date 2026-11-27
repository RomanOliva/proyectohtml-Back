package com.example.ms_warframe.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ms_warframe.DTO.CategoriaDTO;
import com.example.ms_warframe.DTO.CategoriaRequestDTO;
import com.example.ms_warframe.assembler.CategoriaModelAssembler;
import com.example.ms_warframe.service.CategoriaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;
    private final CategoriaModelAssembler assembler;

    @GetMapping
    public ResponseEntity<List<CategoriaDTO>> listar() {
        return ResponseEntity.ok(new ArrayList<>(
                assembler.toCollectionModel(categoriaService.listar()).getContent()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaDTO> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(assembler.toModel(categoriaService.obtener(id)));
    }

    @PostMapping
    public ResponseEntity<CategoriaDTO> crear(@Valid @RequestBody CategoriaRequestDTO request) {
        CategoriaDTO dto = assembler.toModel(categoriaService.crear(request));
        return ResponseEntity
                .created(linkTo(methodOn(CategoriaController.class).obtener(dto.getId())).toUri())
                .body(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaDTO> actualizar(@PathVariable Integer id,
                                                   @Valid @RequestBody CategoriaRequestDTO request) {
        return ResponseEntity.ok(assembler.toModel(categoriaService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        categoriaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

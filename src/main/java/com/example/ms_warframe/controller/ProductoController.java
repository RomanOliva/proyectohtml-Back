package com.example.ms_warframe.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ms_warframe.DTO.ProductoDTO;
import com.example.ms_warframe.DTO.ProductoRequestDTO;
import com.example.ms_warframe.assembler.ProductoAssembler;
import com.example.ms_warframe.service.ItemService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ItemService itemService;
    private final ProductoAssembler assembler;

    @GetMapping
    public ResponseEntity<List<ProductoDTO>> listar(@RequestParam(required = false) String q) {
        return ResponseEntity.ok(itemService.listar(null, null, null, q).stream()
                .map(assembler::toDTO).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(assembler.toDTO(itemService.obtener(id)));
    }

    @PostMapping
    public ResponseEntity<ProductoDTO> crear(@Valid @RequestBody ProductoRequestDTO request) {
        ProductoDTO dto = assembler.toDTO(itemService.crear(assembler.toItemRequest(request)));
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> actualizar(@PathVariable Integer id,
                                                  @Valid @RequestBody ProductoRequestDTO request) {
        return ResponseEntity.ok(assembler.toDTO(
                itemService.actualizar(id, assembler.toItemRequest(request))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        itemService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

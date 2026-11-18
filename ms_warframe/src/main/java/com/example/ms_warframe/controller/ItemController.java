package com.example.ms_warframe.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ms_warframe.DTO.AjusteMasivoDTO;
import com.example.ms_warframe.DTO.AjusteMasivoRequestDTO;
import com.example.ms_warframe.DTO.ItemDTO;
import com.example.ms_warframe.DTO.ItemRequestDTO;
import com.example.ms_warframe.DTO.PrecioRequestDTO;
import com.example.ms_warframe.assembler.ItemModelAssembler;
import com.example.ms_warframe.service.ItemService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final ItemModelAssembler assembler;

    @GetMapping
    public ResponseEntity<CollectionModel<ItemDTO>> listar(
            @RequestParam(required = false) Integer categoria,
            @RequestParam(required = false) Integer min,
            @RequestParam(required = false) Integer max,
            @RequestParam(required = false) String q) {
        return ResponseEntity.ok(assembler.toCollectionModel(itemService.listar(categoria, min, max, q)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDTO> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(assembler.toModel(itemService.obtener(id)));
    }

    @PostMapping
    public ResponseEntity<ItemDTO> crear(@Valid @RequestBody ItemRequestDTO request) {
        ItemDTO dto = assembler.toModel(itemService.crear(request));
        return ResponseEntity
                .created(linkTo(methodOn(ItemController.class).obtener(dto.getId())).toUri())
                .body(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemDTO> actualizar(@PathVariable Integer id,
                                              @Valid @RequestBody ItemRequestDTO request) {
        return ResponseEntity.ok(assembler.toModel(itemService.actualizar(id, request)));
    }

    @PatchMapping("/{id}/precio")
    public ResponseEntity<ItemDTO> actualizarPrecio(@PathVariable Integer id,
                                                    @Valid @RequestBody PrecioRequestDTO request) {
        return ResponseEntity.ok(assembler.toModel(itemService.actualizarPrecio(id, request.getPrecioCreditos())));
    }

    @PostMapping("/ajuste-masivo")
    public ResponseEntity<AjusteMasivoDTO> ajusteMasivo(@Valid @RequestBody AjusteMasivoRequestDTO request) {
        return ResponseEntity.ok(itemService.ajustarPorUmbral(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        itemService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

package com.example.ms_warframe.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ms_warframe.DTO.BoletaDTO;
import com.example.ms_warframe.DTO.BoletaRequestDTO;
import com.example.ms_warframe.service.BoletaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/boletas")
@RequiredArgsConstructor
public class BoletaController {

    private final BoletaService boletaService;

    @PostMapping
    public ResponseEntity<BoletaDTO> crear(Authentication autenticacion,
                                           @Valid @RequestBody BoletaRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(boletaService.crear(autenticacion.getName(), request));
    }

    @GetMapping("/mias")
    public ResponseEntity<List<BoletaDTO>> mias(Authentication autenticacion) {
        return ResponseEntity.ok(boletaService.listarDe(autenticacion.getName()));
    }

    @GetMapping
    public ResponseEntity<List<BoletaDTO>> todas() {
        return ResponseEntity.ok(boletaService.listarTodas());
    }
}

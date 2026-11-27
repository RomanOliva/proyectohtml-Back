package com.example.ms_warframe.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.example.ms_warframe.model.Rol;
import com.example.ms_warframe.repository.UsuarioRepository;
import com.example.ms_warframe.service.AuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminInicial implements ApplicationRunner {

    private final AuthService authService;
    private final UsuarioRepository usuarioRepository;

    @Value("${app.admin.correo:}")
    private String correo;

    @Value("${app.admin.password:}")
    private String password;

    @Override
    public void run(ApplicationArguments args) {
        if (correo.isBlank() || password.isBlank()) {
            log.info("ADMIN_CORREO / ADMIN_PASSWORD no definidos: no se crea administrador inicial");
            return;
        }
        String correoNormal = correo.trim().toLowerCase();
        if (usuarioRepository.existsByCorreo(correoNormal)) {
            return;
        }
        authService.crearUsuario(correoNormal, password, Rol.ADMIN);
        log.info("Administrador inicial creado: {}", correoNormal);
    }
}

package com.example.ms_warframe.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.nimbusds.jwt.JWTClaimsSet;

import com.example.ms_warframe.model.Rol;
import com.example.ms_warframe.model.Usuario;

class JwtServiceTest {

    private static final String SECRETO = "una-clave-de-prueba-con-mas-de-32-caracteres";

    private Usuario usuario() {
        Usuario u = new Usuario();
        u.setCorreo("ana@mail.com");
        u.setRol(Rol.ADMIN);
        return u;
    }

    @Test
    void tokenValidoTrasCrearseConservaCorreoYRol() {
        JwtService jwt = new JwtService(SECRETO, 1);
        String token = jwt.crearToken(usuario());

        JWTClaimsSet claims = jwt.validar(token).orElseThrow();
        assertEquals("ana@mail.com", claims.getSubject());
        assertEquals("ADMIN", claims.getClaim("rol"));
    }

    @Test
    void tokenFirmadoConOtraClaveSeRechaza() {
        String token = new JwtService("otra-clave-distinta-de-mas-de-32-caracteres", 1).crearToken(usuario());
        assertTrue(new JwtService(SECRETO, 1).validar(token).isEmpty());
    }

    @Test
    void tokenVencidoSeRechaza() {
        JwtService jwt = new JwtService(SECRETO, -1);
        assertTrue(jwt.validar(jwt.crearToken(usuario())).isEmpty());
    }

    @Test
    void textoQueNoEsTokenSeRechaza() {
        assertTrue(new JwtService(SECRETO, 1).validar("esto-no-es-un-jwt").isEmpty());
    }

    @Test
    void secretoCortoHaceFallarElArranque() {
        JwtService jwt = new JwtService("corto", 1);
        assertThrows(IllegalStateException.class, jwt::validarSecreto);
    }
}

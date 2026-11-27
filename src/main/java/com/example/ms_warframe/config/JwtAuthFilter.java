package com.example.ms_warframe.config;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.nimbusds.jwt.JWTClaimsSet;

import com.example.ms_warframe.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthFilter extends OncePerRequestFilter {

    private static final String PREFIJO = "Bearer ";

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String cabecera = request.getHeader("Authorization");
        if (cabecera != null && cabecera.startsWith(PREFIJO)) {
            jwtService.validar(cabecera.substring(PREFIJO.length()).trim()).ifPresent(this::autenticar);
        }
        chain.doFilter(request, response);
    }

    private void autenticar(JWTClaimsSet claims) {
        Object rol = claims.getClaim("rol");
        if (claims.getSubject() == null || rol == null) {
            return;
        }

        var autoridades = List.of(new SimpleGrantedAuthority("ROLE_" + rol));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(claims.getSubject(), null, autoridades));
    }
}

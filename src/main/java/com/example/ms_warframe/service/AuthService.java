package com.example.ms_warframe.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ms_warframe.DTO.AuthDTO;
import com.example.ms_warframe.DTO.LoginRequestDTO;
import com.example.ms_warframe.DTO.RegistroRequestDTO;
import com.example.ms_warframe.DTO.UsuarioDTO;
import com.example.ms_warframe.exception.ConflictoException;
import com.example.ms_warframe.exception.CredencialesInvalidasException;
import com.example.ms_warframe.model.Rol;
import com.example.ms_warframe.model.Usuario;
import com.example.ms_warframe.repository.UsuarioRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthService {

    private static final String MENSAJE_LOGIN = "Correo o contraseña incorrectos.";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private final String hashDeRelleno;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.hashDeRelleno = passwordEncoder.encode("relleno-no-es-una-contraseña");
    }

    @Transactional
    public UsuarioDTO registrar(RegistroRequestDTO request) {
        String correo = normalizar(request.getCorreo());
        if (usuarioRepository.existsByCorreo(correo)) {
            throw new ConflictoException("Ese correo ya está registrado.");
        }

        Usuario usuario = crearUsuario(correo, request.getPassword(), Rol.CLIENTE);
        log.info("Usuario registrado: {}", correo);
        return new UsuarioDTO(usuario.getCorreo(), usuario.getRol().name());
    }

    @Transactional(readOnly = true)
    public AuthDTO login(LoginRequestDTO request) {
        String correo = normalizar(request.getCorreo());
        Usuario usuario = usuarioRepository.findByCorreo(correo).orElse(null);

        String hash = usuario != null ? usuario.getPasswordHash() : hashDeRelleno;
        boolean coincide = passwordEncoder.matches(request.getPassword(), hash);
        if (usuario == null || !coincide) {
            log.warn("Login fallido para {}", correo);
            throw new CredencialesInvalidasException(MENSAJE_LOGIN);
        }
        return new AuthDTO(jwtService.crearToken(usuario), usuario.getCorreo(), usuario.getRol().name());
    }

    @Transactional
    public Usuario crearUsuario(String correo, String password, Rol rol) {
        Usuario usuario = new Usuario();
        usuario.setCorreo(correo);
        usuario.setPasswordHash(passwordEncoder.encode(password));
        usuario.setRol(rol);
        return usuarioRepository.save(usuario);
    }

    private String normalizar(String correo) {
        return correo.trim().toLowerCase();
    }
}

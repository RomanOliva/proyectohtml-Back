package com.example.ms_warframe.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.ms_warframe.DTO.AuthDTO;
import com.example.ms_warframe.DTO.LoginRequestDTO;
import com.example.ms_warframe.DTO.RegistroRequestDTO;
import com.example.ms_warframe.DTO.UsuarioDTO;
import com.example.ms_warframe.exception.ConflictoException;
import com.example.ms_warframe.exception.CredencialesInvalidasException;
import com.example.ms_warframe.model.Rol;
import com.example.ms_warframe.model.Usuario;
import com.example.ms_warframe.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private JwtService jwtService;

    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    private AuthService authService;

    @BeforeEach
    void preparar() {
        authService = new AuthService(usuarioRepository, encoder, jwtService);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));
    }

    private RegistroRequestDTO registro(String correo, String password) {
        RegistroRequestDTO dto = new RegistroRequestDTO();
        dto.setCorreo(correo);
        dto.setPassword(password);
        return dto;
    }

    private LoginRequestDTO login(String correo, String password) {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setCorreo(correo);
        dto.setPassword(password);
        return dto;
    }

    @Test
    void registrarCreaClienteConHashYCorreoNormalizado() {
        when(usuarioRepository.existsByCorreo("ana@mail.com")).thenReturn(false);

        UsuarioDTO resultado = authService.registrar(registro("  Ana@Mail.COM ", "secreto1"));

        assertEquals("ana@mail.com", resultado.getCorreo());
        assertEquals("CLIENTE", resultado.getRol());
        org.mockito.ArgumentCaptor<Usuario> captor = org.mockito.ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());

        assertEquals(false, captor.getValue().getPasswordHash().contains("secreto1"));
        assertEquals(true, encoder.matches("secreto1", captor.getValue().getPasswordHash()));
    }

    @Test
    void registrarCorreoRepetidoLanzaConflicto() {
        when(usuarioRepository.existsByCorreo("ana@mail.com")).thenReturn(true);

        assertThrows(ConflictoException.class, () -> authService.registrar(registro("ana@mail.com", "secreto1")));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void loginCorrectoDevuelveTokenYRol() {
        Usuario u = new Usuario();
        u.setCorreo("ana@mail.com");
        u.setRol(Rol.VENDEDOR);
        u.setPasswordHash(encoder.encode("secreto1"));
        when(usuarioRepository.findByCorreo("ana@mail.com")).thenReturn(Optional.of(u));
        when(jwtService.crearToken(u)).thenReturn("TOKEN");

        AuthDTO auth = authService.login(login("ANA@mail.com", "secreto1"));

        assertEquals("TOKEN", auth.getToken());
        assertEquals("VENDEDOR", auth.getRol());
        assertEquals("ana@mail.com", auth.getCorreo());
    }

    @Test
    void loginConPasswordIncorrectaLanzaCredencialesInvalidas() {
        Usuario u = new Usuario();
        u.setCorreo("ana@mail.com");
        u.setRol(Rol.CLIENTE);
        u.setPasswordHash(encoder.encode("secreto1"));
        when(usuarioRepository.findByCorreo("ana@mail.com")).thenReturn(Optional.of(u));

        assertThrows(CredencialesInvalidasException.class, () -> authService.login(login("ana@mail.com", "mala")));
    }

    @Test
    void loginConCorreoInexistenteDaElMismoError() {
        when(usuarioRepository.findByCorreo("nadie@mail.com")).thenReturn(Optional.empty());

        CredencialesInvalidasException ex = assertThrows(CredencialesInvalidasException.class,
                () -> authService.login(login("nadie@mail.com", "secreto1")));
        assertEquals("Correo o contraseña incorrectos.", ex.getMessage());
    }
}

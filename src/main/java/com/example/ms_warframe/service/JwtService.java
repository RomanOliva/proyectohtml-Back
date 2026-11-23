package com.example.ms_warframe.service;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import com.example.ms_warframe.model.Usuario;

import jakarta.annotation.PostConstruct;

@Service
public class JwtService {

    private static final int LARGO_MINIMO_SECRETO = 32;

    private final byte[] secreto;
    private final long horasDeVida;

    public JwtService(@Value("${app.jwt.secret}") String secreto,
                      @Value("${app.jwt.horas:1}") long horasDeVida) {
        this.secreto = secreto.getBytes(StandardCharsets.UTF_8);
        this.horasDeVida = horasDeVida;
    }

    @PostConstruct
    void validarSecreto() {
        if (secreto.length < LARGO_MINIMO_SECRETO) {
            throw new IllegalStateException(
                    "JWT_SECRET debe tener al menos " + LARGO_MINIMO_SECRETO + " caracteres");
        }
    }

    public String crearToken(Usuario usuario) {
        Instant ahora = Instant.now();
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(usuario.getCorreo())
                .claim("rol", usuario.getRol().name())
                .issueTime(Date.from(ahora))
                .expirationTime(Date.from(ahora.plusSeconds(horasDeVida * 3600)))
                .build();
        try {
            SignedJWT jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
            jwt.sign(new MACSigner(secreto));
            return jwt.serialize();
        } catch (JOSEException e) {
            throw new IllegalStateException("No se pudo firmar el token", e);
        }
    }

    public Optional<JWTClaimsSet> validar(String token) {
        try {
            SignedJWT jwt = SignedJWT.parse(token);
            if (!jwt.verify(new MACVerifier(secreto))) {
                return Optional.empty();
            }
            JWTClaimsSet claims = jwt.getJWTClaimsSet();
            Date vence = claims.getExpirationTime();
            if (vence == null || vence.before(new Date())) {
                return Optional.empty();
            }
            return Optional.of(claims);
        } catch (ParseException | JOSEException e) {
            return Optional.empty();
        }
    }
}

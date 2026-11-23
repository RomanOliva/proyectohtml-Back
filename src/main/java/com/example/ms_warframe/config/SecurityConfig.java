package com.example.ms_warframe.config;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.ms_warframe.service.JwtService;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig {

    private static final String[] ESCRITURA_CATALOGO = { "/api/v1/productos/**", "/api/v1/items/**" };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filtros(HttpSecurity http, JwtService jwtService) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(a -> a
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/error").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/login", "/api/v1/auth/register").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/productos/**", "/api/v1/categorias/**",
                        "/api/v1/items/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/items/ajuste-masivo").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, ESCRITURA_CATALOGO).hasRole("ADMIN")
                .requestMatchers("/api/v1/categorias/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/v1/boletas").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, ESCRITURA_CATALOGO).hasAnyRole("ADMIN", "VENDEDOR")
                .requestMatchers(HttpMethod.PUT, ESCRITURA_CATALOGO).hasAnyRole("ADMIN", "VENDEDOR")
                .requestMatchers(HttpMethod.PATCH, ESCRITURA_CATALOGO).hasAnyRole("ADMIN", "VENDEDOR")
                .anyRequest().authenticated())
            .exceptionHandling(e -> e
                .authenticationEntryPoint((req, res, ex) -> escribirError(res, 401,
                        "Tu sesión no es válida. Inicia sesión."))
                .accessDeniedHandler((req, res, ex) -> escribirError(res, 403,
                        "No tienes permisos para realizar esta acción.")))
            .addFilterBefore(new JwtAuthFilter(jwtService), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(@Value("${app.cors.origins}") List<String> origenes) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(origenes.stream().map(String::trim).toList());
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource fuente = new UrlBasedCorsConfigurationSource();
        fuente.registerCorsConfiguration("/**", config);
        return fuente;
    }

    private void escribirError(HttpServletResponse res, int estado, String mensaje) throws java.io.IOException {
        res.setStatus(estado);
        res.setContentType("application/json");
        res.setCharacterEncoding(StandardCharsets.UTF_8.name());
        res.getWriter().write("{\"status\":" + estado + ",\"mensaje\":\"" + mensaje
                + "\",\"detalles\":[],\"fecha\":\"" + Instant.now() + "\"}");
    }
}

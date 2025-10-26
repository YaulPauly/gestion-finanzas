package com.fintrack.fintrackbackend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    /**
     * 🔐 Configura toda la seguridad HTTP para la API:
     * - Define qué rutas son públicas
     * - Protege todas las demás con autenticación JWT
     * - Registra el filtro personalizado JwtAuthFilter
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // 1️⃣ Desactiva CSRF porque en APIs REST con JWT no se necesita
                .csrf(csrf -> csrf.disable())

                // 2️⃣ Define las reglas de acceso a las rutas
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll() // 🔓 Rutas públicas (login, registro, etc.)
                        .anyRequest().authenticated()               // 🔐 Todas las demás requieren JWT
                )

                // 3️⃣ Inserta nuestro filtro JWT antes del filtro de autenticación por defecto
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                // 4️⃣ Construye y devuelve la configuración de seguridad
                .build();
    }
}

package com.cibertec.SkillsFest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Desactivar CSRF: Vital para que Postman y React puedan hacer POST
            .csrf(AbstractHttpConfigurer::disable)
            
            // 2. Configurar las rutas
            .authorizeHttpRequests(auth -> auth
                // Dejamos pública nuestra ruta de prueba por ahora
                .requestMatchers("/api/equipos/**").permitAll() 
                // --- NUEVA LÍNEA PARA LAS SEDES ---
                .requestMatchers("/api/sedes/**").permitAll() 
                
                .requestMatchers("/api/eventos/**").permitAll()
                           
                .anyRequest().authenticated()
            );

        return http.build();
    }
}

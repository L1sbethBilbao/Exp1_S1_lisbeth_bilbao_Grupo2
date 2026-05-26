package com.duoc.inscripcion_cursos.config;

import com.duoc.inscripcion_cursos.security.SecurityRoles;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Profile("!init-db")
public class SecurityConfig {

    @Value("${app.security.admin.username:admin}")
    private String adminUsername;

    @Value("${app.security.admin.password:Admin2026!}")
    private String adminPassword;

    @Value("${app.security.estudiante.username:estudiante}")
    private String estudianteUsername;

    @Value("${app.security.estudiante.password:Estudiante2026!}")
    private String estudiantePassword;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health", "/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/cursos").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/cursos").hasRole(SecurityRoles.ADMIN)
                        .requestMatchers(HttpMethod.POST, "/api/inscripciones").hasRole(SecurityRoles.ESTUDIANTE)
                        .requestMatchers("/api/admin/**").hasRole(SecurityRoles.ADMIN)
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        return new InMemoryUserDetailsManager(
                User.builder()
                        .username(adminUsername)
                        .password(passwordEncoder.encode(adminPassword))
                        .roles(SecurityRoles.ADMIN)
                        .build(),
                User.builder()
                        .username(estudianteUsername)
                        .password(passwordEncoder.encode(estudiantePassword))
                        .roles(SecurityRoles.ESTUDIANTE)
                        .build()
        );
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

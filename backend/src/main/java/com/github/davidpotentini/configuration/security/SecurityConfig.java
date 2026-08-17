package com.github.davidpotentini.configuration.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Esqueleto de segurança reaproveitado do projeto antigo: STATELESS, CSRF desligado,
 * CORS (bean {@code CorsConfig}) e o {@link JwtAuthenticationFilter} antes do filtro
 * de usuário/senha.
 *
 * <p><b>Mudança em relação ao antigo:</b> o bloco {@code authorizeHttpRequests} não lista
 * mais rotas por {@code hasRole(...)}. A autorização fina é <b>papel × recurso × estado</b>,
 * feita pelo interceptor {@code @RequerPermissao} (ver ESTRUTURA-BACKEND.md §7). Aqui só se
 * libera o que é público (login/cadastro/OPTIONS) e exige autenticação no resto.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(a -> a
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/login/**", "/cadastro/**").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

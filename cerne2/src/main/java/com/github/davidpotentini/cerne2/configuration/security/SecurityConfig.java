package com.github.davidpotentini.cerne2.configuration.security;

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


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFIlter;

    public SecurityConfig(JwtAuthenticationFilter jwtFIlter){
        this.jwtFIlter = jwtFIlter;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csfr -> csfr.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(a -> a
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/login/**", "/cadastro/**").permitAll()

                        .requestMatchers("/incubadas/*/planejamento/**")
                        .hasAnyRole("ADMIN_INCUBADA","ADMIN_INCUBADORA")

                        .requestMatchers(
                                "/gerenciaIncubadas/**",
                                "/metricas/**",
                                "/formularios/**",
                                "/servicosValorAgregado/**",
                                "/planejamento/**"
                                )
                        .hasRole("ADMIN_INCUBADORA")

                        .requestMatchers("/pessoas/incubada/{incCod}")
                        .hasAnyRole("ADMIN_INCUBADA","ADMIN_INCUBADORA")

                        .requestMatchers("/pessoas/**"
                        )
                        .hasRole("ADMIN_INCUBADORA")

                        .requestMatchers(
                                "/incubadas/*/ambienteCanvas/**",
                                "/incubadas/*/validacaoHipotese/**"
                                )
                        .hasRole("ADMIN_INCUBADA")

                        .anyRequest().authenticated())
                .addFilterBefore(jwtFIlter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

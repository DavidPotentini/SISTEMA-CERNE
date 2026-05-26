package com.github.davidpotentini.cerne2.configuration.security;

import com.github.davidpotentini.cerne2.configuration.multitenancy.TenantContext;
import com.github.davidpotentini.cerne2.dto.login.UsuarioAutenticadoDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String PREFIXO_BEARER = "Bearer ";

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService){
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header != null && header.startsWith(PREFIXO_BEARER)) {
            try {
                Claims c = jwtService.validar(header.substring(PREFIXO_BEARER.length()));

                UsuarioAutenticadoDTO principal = new UsuarioAutenticadoDTO(
                        c.get("usnCod", Long.class),
                        c.get("pessoaCod", Long.class),
                        c.getSubject(),
                        c.get("tntCod", Long.class),
                        c.get("nomeSchema", String.class),
                        c.get("role", String.class),
                        c.get("incCod", Long.class)
                );

                var auth = new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + principal.role()))
                );

                SecurityContextHolder.getContext().setAuthentication(auth);
                TenantContext.set(principal.nomeSchema());


            } catch (JwtException ex) {
                SecurityContextHolder.clearContext();
            }

        }

        chain.doFilter(request, response);
    }
}

package com.github.davidpotentini.configuration.security;
import com.github.davidpotentini.comum.tenant.UsuarioAutenticado;
import com.github.davidpotentini.comum.tenant.SessaoContext;

import com.github.davidpotentini.comum.tenant.TenantContext;
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

/**
 * Lê o Bearer token e popula, por requisição, o {@code SecurityContext}, o {@link TenantContext}
 * (schema do Hibernate) e o {@link SessaoContext}. A autorização fina fica no interceptor
 * {@code @RequerPermissao} (papel × recurso), não aqui.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String PREFIXO_BEARER = "Bearer ";

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
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

                UsuarioAutenticado principal = new UsuarioAutenticado(
                        c.get("ctaCod", Long.class),
                        c.getSubject(),
                        c.get("nomeSchema", String.class),
                        c.get("papCod", Long.class),
                        c.get("papelNome", String.class),
                        Boolean.TRUE.equals(c.get("adminPlataforma", Boolean.class))
                );

                // Authority informativa; a checagem fina é por recurso no interceptor, não em hasRole(...).
                String papel = principal.papelNome() != null ? principal.papelNome() : "SEM_PAPEL";
                var auth = new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        List.of(new SimpleGrantedAuthority("PAPEL_" + papel))
                );

                SecurityContextHolder.getContext().setAuthentication(auth);
                TenantContext.set(principal.nomeSchema());
                SessaoContext.set(principal);

                chain.doFilter(request, response);
                return;

            } catch (JwtException ex) {
                SecurityContextHolder.clearContext();
            } finally {
                SessaoContext.clear();
                // TenantContext é limpo pelo TenantFilter (finally), que envolve a cadeia.
            }
        }

        chain.doFilter(request, response);
    }
}

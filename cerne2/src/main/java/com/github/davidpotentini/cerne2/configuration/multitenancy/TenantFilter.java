package com.github.davidpotentini.cerne2.configuration.multitenancy;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TenantFilter extends OncePerRequestFilter {

    public static final String TENANT_HEADER = "X-Tenant";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // O JwtAuthenticationFilter (dentro do SecurityFilterChain) roda antes
            // e popula TenantContext via claim. Só usamos o header como fallback
            // para chamadas não-autenticadas (login/cadastro).

            if(TenantContext.get() == null) {
                String tenant = request.getHeader(TENANT_HEADER);
                if (tenant != null && !tenant.isBlank()) {
                    TenantContext.set(tenant);
                }
            }
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}

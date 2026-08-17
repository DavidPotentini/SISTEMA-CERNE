package com.github.davidpotentini.comum.autorizacao;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Registra o {@link PermissaoInterceptor} em todas as rotas. */
@Configuration
public class AutorizacaoWebMvcConfig implements WebMvcConfigurer {

    private final PermissaoInterceptor permissaoInterceptor;

    public AutorizacaoWebMvcConfig(PermissaoInterceptor permissaoInterceptor) {
        this.permissaoInterceptor = permissaoInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(permissaoInterceptor);
    }
}

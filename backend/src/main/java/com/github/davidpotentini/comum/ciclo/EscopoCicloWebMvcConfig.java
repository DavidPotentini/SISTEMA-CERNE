package com.github.davidpotentini.comum.ciclo;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Registra o {@link EscopoCicloInterceptor} em todas as rotas — a anotação {@link EscopoCiclo} decide onde vale. */
@Configuration
public class EscopoCicloWebMvcConfig implements WebMvcConfigurer {

    private final EscopoCicloInterceptor escopoCicloInterceptor;

    public EscopoCicloWebMvcConfig(EscopoCicloInterceptor escopoCicloInterceptor) {
        this.escopoCicloInterceptor = escopoCicloInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(escopoCicloInterceptor);
    }
}

package com.github.davidpotentini.comum.autorizacao;

import com.github.davidpotentini.enums.ENivel;
import com.github.davidpotentini.enums.ERecurso;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Exige, no método do controller, que o papel logado tenha pelo menos {@code nivel} no
 * {@code recurso}. Sem a anotação, a rota é livre. Verificado pelo {@link PermissaoInterceptor}.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequerPermissao {
    ERecurso recurso();

    ENivel nivel() default ENivel.LEITURA;
}

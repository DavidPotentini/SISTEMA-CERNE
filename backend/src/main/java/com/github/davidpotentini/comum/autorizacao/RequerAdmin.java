package com.github.davidpotentini.comum.autorizacao;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Exige que o usuário logado seja administrador da plataforma
 * ({@code CONTAS.ADMIN_PLATAFORMA = true}). Vale no método ou na classe do controller.
 * Rotas de admin não usam papel × recurso (o admin não tem papel de tenant).
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequerAdmin {
}

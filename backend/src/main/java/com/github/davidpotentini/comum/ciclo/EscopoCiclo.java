package com.github.davidpotentini.comum.ciclo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marca um controller cujas mutações são barradas quando o ciclo em foco está {@code ENCERRADO}
 * (ver {@link EscopoCicloInterceptor}). Fica de fora de propósito: {@code CicloController} (pôr em
 * foco/criar não pode se autobloquear) e {@code MetodologiaController} (template global, sempre editável).
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EscopoCiclo {
}

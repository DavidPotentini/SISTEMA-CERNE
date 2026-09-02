package com.github.davidpotentini.comum.ciclo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marca um controller cujas operações agem sobre o ciclo em foco. O {@link EscopoCicloInterceptor}
 * deixa passar qualquer GET (navegar o histórico de qualquer ciclo), mas corta mutações
 * (POST/PUT/PATCH/DELETE) quando o ciclo em foco está {@code ENCERRADO} — ciclo encerrado é somente
 * leitura (registro histórico).
 *
 * <p>Fica de fora de propósito: {@code CicloController} (pôr em foco/criar não pode se autobloquear)
 * e {@code MetodologiaController} (template global, sempre editável).
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EscopoCiclo {
}

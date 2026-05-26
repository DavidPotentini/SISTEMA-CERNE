package com.github.davidpotentini.cerne2.configuration.security.annotations;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('ADMIN_INCUBADORA') or " +
              "(hasRole('ADMIN_INCUBADA') and #incCod == authentication.principal.incCod)")
public @interface AcessoIncubada {
}

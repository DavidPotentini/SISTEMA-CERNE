package com.github.davidpotentini.cerne2.configuration.multitenancy;

import java.util.function.Supplier;

public final class TenantContext {

    private static final ThreadLocal<String> CURRENT = new ThreadLocal<>();

    private TenantContext() {
    }

    public static void set(String schema) {
        CURRENT.set(schema);
    }

    public static String get() {
        return CURRENT.get();
    }

    public static void clear() {
        CURRENT.remove();
    }

    /**
     * Executa a ação com o schema informado ativo, restaurando o tenant anterior
     * ao final. O schema precisa estar definido <b>antes</b> de qualquer operação
     * transacional ({@code REQUIRES_NEW}), pois o Hibernate resolve o tenant na
     * abertura da sessão.
     */
    public static <T> T callWithin(String schema, Supplier<T> action) {
        String previous = get();
        set(schema);
        try {
            return action.get();
        } finally {
            if (previous == null) {
                clear();
            } else {
                set(previous);
            }
        }
    }

    public static void runWithin(String schema, Runnable action) {
        callWithin(schema, () -> {
            action.run();
            return null;
        });
    }
}

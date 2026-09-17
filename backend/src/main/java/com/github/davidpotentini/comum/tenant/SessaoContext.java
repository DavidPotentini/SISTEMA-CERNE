package com.github.davidpotentini.comum.tenant;

/** Identidade da sessão por requisição (ThreadLocal), populada pelo {@link JwtAuthenticationFilter}. */
public final class SessaoContext {

    private static final ThreadLocal<UsuarioAutenticado> CURRENT = new ThreadLocal<>();

    private SessaoContext() {
    }

    public static void set(UsuarioAutenticado usuario) {
        CURRENT.set(usuario);
    }

    public static UsuarioAutenticado get() {
        return CURRENT.get();
    }

    public static void clear() {
        CURRENT.remove();
    }

    public static Long papelAtual() {
        UsuarioAutenticado u = CURRENT.get();
        return u != null ? u.papCod() : null;
    }

    public static Long contaAtual() {
        UsuarioAutenticado u = CURRENT.get();
        return u != null ? u.ctaCod() : null;
    }

    public static boolean adminAtual() {
        UsuarioAutenticado u = CURRENT.get();
        return u != null && u.adminPlataforma();
    }
}

package com.github.davidpotentini.comum.tenant;

/**
 * Contexto da sessão por requisição (ThreadLocal), populado pelo
 * {@link JwtAuthenticationFilter} a partir dos claims do token. Expõe a conta e o
 * papel atuais para o corte de autorização (papel × recurso) e para o registro de
 * eventos de domínio (ator = {@code contaAtual()}).
 *
 * <p>Análogo ao {@code TenantContext}, mas carregando a identidade em vez do schema.
 */
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

    /** Papel local do usuário logado ({@code PAP_COD}); {@code null} se não houver sessão/papel. */
    public static Long papelAtual() {
        UsuarioAutenticado u = CURRENT.get();
        return u != null ? u.papCod() : null;
    }

    /** Conta global do usuário logado ({@code CTA_COD}); {@code null} se não houver sessão. */
    public static Long contaAtual() {
        UsuarioAutenticado u = CURRENT.get();
        return u != null ? u.ctaCod() : null;
    }

    /** Se o usuário logado é administrador da plataforma. */
    public static boolean adminAtual() {
        UsuarioAutenticado u = CURRENT.get();
        return u != null && u.adminPlataforma();
    }
}

package com.github.davidpotentini.comum.autorizacao;

import com.github.davidpotentini.comum.erro.AcessoNegadoException;
import com.github.davidpotentini.comum.tenant.SessaoContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Corta a requisição antes do controller: se o método exige permissão
 * ({@link RequerPermissao}) e o papel logado ({@link SessaoContext#papelAtual()}) não a
 * tem, lança {@link AcessoNegadoException} (→ 403, tratada em {@code comum/erro}).
 */
@Component
public class PermissaoInterceptor implements HandlerInterceptor {

    private final PermissaoService permissoes;

    public PermissaoInterceptor(PermissaoService permissoes) {
        this.permissoes = permissoes;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod hm)) {
            return true;
        }
        // Rotas de admin da plataforma: exigem ADMIN_PLATAFORMA e não usam papel × recurso.
        if (hm.hasMethodAnnotation(RequerAdmin.class)
                || hm.getBeanType().isAnnotationPresent(RequerAdmin.class)) {
            if (!SessaoContext.adminAtual()) {
                throw new AcessoNegadoException("Requer administrador da plataforma.");
            }
            return true;
        }
        RequerPermissao anot = hm.getMethodAnnotation(RequerPermissao.class);
        if (anot == null) {
            return true; // rota sem anotação = livre
        }
        Long papel = SessaoContext.papelAtual();
        if (!permissoes.permite(papel, anot.recurso(), anot.nivel())) {
            throw new AcessoNegadoException(anot.recurso());
        }
        return true;
    }
}

package com.github.davidpotentini.comum.ciclo;

import com.github.davidpotentini.comum.erro.RegraNegocioException;
import com.github.davidpotentini.enums.EStatusCiclo;
import com.github.davidpotentini.repository.ciclos.CiclosRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Corta a requisição antes de um controller marcado com {@link EscopoCiclo}: GET sempre passa
 * (navegar o histórico de qualquer ciclo), mas mutação só passa quando o ciclo em foco está
 * {@code ATIVO}; se o foco é um ciclo {@code ENCERRADO}, lança {@link RegraNegocioException} (→409).
 * Sem ciclo em foco, deixa passar — a escrita cai no ciclo {@code ATIVO} (ver {@code cicloEmFoco()}
 * nos services), que é editável.
 *
 * <p>Registrado em todas as rotas (como o {@code PermissaoInterceptor}); a anotação no controller é
 * que decide onde vale — sem lista de rotas no config, para não quebrar em refactor de URL.
 */
@Component
public class EscopoCicloInterceptor implements HandlerInterceptor {

    private final CiclosRepository ciclos;

    public EscopoCicloInterceptor(CiclosRepository ciclos) {
        this.ciclos = ciclos;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod hm)) {
            return true;
        }
        if (!hm.getBeanType().isAnnotationPresent(EscopoCiclo.class)) {
            return true; // controller sem anotação = fora do escopo de ciclo
        }
        if (!metodoDeEscrita(request.getMethod())) {
            return true; // leitura livre em qualquer ciclo
        }
        ciclos.findByEmFocoTrue().ifPresent(foco -> {
            if (foco.getStatus() != EStatusCiclo.ATIVO) {
                throw new RegraNegocioException(
                        "O ciclo em foco está encerrado (somente leitura). "
                                + "Ponha o ciclo ativo em foco para editar.");
            }
        });
        return true;
    }

    private boolean metodoDeEscrita(String metodo) {
        return HttpMethod.POST.matches(metodo)
                || HttpMethod.PUT.matches(metodo)
                || HttpMethod.PATCH.matches(metodo)
                || HttpMethod.DELETE.matches(metodo);
    }
}

package com.github.davidpotentini.comum.ciclo;

import com.github.davidpotentini.enums.EStatusCiclo;
import com.github.davidpotentini.model.ciclos.CiclosModel;
import com.github.davidpotentini.repository.ciclos.CiclosRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Resolve qual ciclo as telas dependentes de ciclo devem refletir. Centraliza a regra que antes se
 * repetia em cada service: o ciclo {@code EM_FOCO}; se nenhum estiver em foco, cai no {@code ATIVO}
 * (editável). A escrita em ciclo encerrado é barrada antes, pelo {@link EscopoCicloInterceptor}.
 */
@Component
public class CicloContexto {

    private final CiclosRepository ciclos;

    public CicloContexto(CiclosRepository ciclos) {
        this.ciclos = ciclos;
    }

    /** Ciclo refletido nas telas (EM_FOCO, ou o ATIVO como fallback), ou {@code null} se não houver ciclo. */
    public CiclosModel emFoco() {
        return ciclos.findByEmFocoTrue().orElseGet(() -> {
            List<CiclosModel> ativos = ciclos.findByStatus(EStatusCiclo.ATIVO);
            return ativos.isEmpty() ? null : ativos.get(0);
        });
    }
}

package com.github.davidpotentini.comum.ciclo;

import com.github.davidpotentini.enums.EStatusCiclo;
import com.github.davidpotentini.model.ciclos.CiclosModel;
import com.github.davidpotentini.repository.ciclos.CiclosRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/** Ciclo que as telas refletem: o {@code EM_FOCO}; sem foco, cai no {@code ATIVO} (editável). */
@Component
public class CicloContexto {

    private final CiclosRepository ciclos;

    public CicloContexto(CiclosRepository ciclos) {
        this.ciclos = ciclos;
    }

    public CiclosModel emFoco() {
        return ciclos.findByEmFocoTrue().orElseGet(() -> {
            List<CiclosModel> ativos = ciclos.findByStatus(EStatusCiclo.ATIVO);
            return ativos.isEmpty() ? null : ativos.get(0);
        });
    }
}

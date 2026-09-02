package com.github.davidpotentini.repository.ciclos;

import com.github.davidpotentini.model.ciclos.CicloEmpreendimentoId;
import com.github.davidpotentini.model.ciclos.CicloEmpreendimentoModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CicloEmpreendimentoRepository
        extends JpaRepository<CicloEmpreendimentoModel, CicloEmpreendimentoId> {

    /** Empreendimentos participantes de um ciclo. */
    List<CicloEmpreendimentoModel> findByCicCod(Long cicCod);

    /** Limpa os participantes de um ciclo (antes de regravar a seleção ao gerar). */
    void deleteByCicCod(Long cicCod);
}

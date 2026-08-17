package com.github.davidpotentini.repository.indicador;

import com.github.davidpotentini.enums.EOrigemIndicador;
import com.github.davidpotentini.model.indicador.IndicadorModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IndicadorRepository extends JpaRepository<IndicadorModel, Long> {

    /** Indicadores do ciclo, em ordem alfabética (listagem da aba "Indicadores do ciclo"). */
    List<IndicadorModel> findByCicCodOrderByNomeAsc(Long cicCod);

    /** Remove os indicadores de uma origem no ciclo — usado para regerar os da metodologia. */
    void deleteByCicCodAndOrigem(Long cicCod, EOrigemIndicador origem);
}

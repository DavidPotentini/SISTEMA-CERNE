package com.github.davidpotentini.repository.metodologia;

import com.github.davidpotentini.model.metodologia.IndicadorMetodologiaModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface IndicadorMetodologiaRepository extends JpaRepository<IndicadorMetodologiaModel, Long> {

    /** Indicadores das práticas informadas (as da versão), em ordem alfabética. */
    List<IndicadorMetodologiaModel> findByPrtCodInOrderByNomeAsc(Collection<Long> prtCods);

    /** Indicadores de uma prática — usado ao clonar a árvore na publicação. */
    List<IndicadorMetodologiaModel> findByPrtCod(Long prtCod);
}

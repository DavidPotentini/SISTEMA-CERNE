package com.github.davidpotentini.repository.metodologia;

import com.github.davidpotentini.model.metodologia.AtividadeMetodologiaModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface AtividadeMetodologiaRepository extends JpaRepository<AtividadeMetodologiaModel, Long> {

    /** Atividades das práticas informadas, em ordem alfabética. */
    List<AtividadeMetodologiaModel> findByPrtCodInOrderByNomeAsc(Collection<Long> prtCods);

    /** Atividades de uma prática — usado ao materializar a metodologia no ciclo. */
    List<AtividadeMetodologiaModel> findByPrtCod(Long prtCod);
}

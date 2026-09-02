package com.github.davidpotentini.repository.metodologia;

import com.github.davidpotentini.model.metodologia.AtividadeMetodologiaModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AtividadeMetodologiaRepository extends JpaRepository<AtividadeMetodologiaModel, Long> {

    /** Atividades das práticas informadas, na ordem de exibição ({@code ordem}) dentro de cada prática. */
    List<AtividadeMetodologiaModel> findByPrtCodInOrderByOrdemAscNomeAsc(Collection<Long> prtCods);

    /** Atividade de maior {@code ordem} na prática — para anexar a próxima no fim. */
    Optional<AtividadeMetodologiaModel> findFirstByPrtCodOrderByOrdemDesc(Long prtCod);

    /** Atividades de uma prática — usado ao materializar a metodologia no ciclo. */
    List<AtividadeMetodologiaModel> findByPrtCod(Long prtCod);

    /** Há alguma atividade neste agrupamento? (trava para excluir o agrupamento). */
    boolean existsByAgrCod(Long agrCod);
}

package com.github.davidpotentini.repository.metodologia;

import com.github.davidpotentini.model.metodologia.AgrupamentoModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AgrupamentoRepository extends JpaRepository<AgrupamentoModel, Long> {

    /** Agrupamentos das práticas informadas, na ordem de exibição ({@code ordem}) dentro de cada prática. */
    List<AgrupamentoModel> findByPrtCodInOrderByOrdemAscAgrCodAsc(Collection<Long> prtCods);

    /** Agrupamentos de uma prática, na ordem de exibição — usado ao materializar e ao reordenar. */
    List<AgrupamentoModel> findByPrtCodOrderByOrdemAscAgrCodAsc(Long prtCod);

    /** Agrupamento de maior {@code ordem} na prática — para anexar o próximo no fim. */
    Optional<AgrupamentoModel> findFirstByPrtCodOrderByOrdemDesc(Long prtCod);
}

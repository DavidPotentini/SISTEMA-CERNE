package com.github.davidpotentini.repository.metodologia;

import com.github.davidpotentini.model.metodologia.PraticaModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PraticaRepository extends JpaRepository<PraticaModel, Long> {

    /** Práticas de um processo, na ordem de exibição ({@code ordem}). */
    List<PraticaModel> findByPrcCodOrderByOrdemAscPrtCodAsc(Long prcCod);

    /** Prática de maior {@code ordem} no processo — para anexar a próxima no fim. */
    Optional<PraticaModel> findFirstByPrcCodOrderByOrdemDesc(Long prcCod);

    /** Práticas dos processos informados (as da versão) — usada para reunir os indicadores da versão. */
    List<PraticaModel> findByPrcCodIn(Collection<Long> prcCods);
}

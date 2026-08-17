package com.github.davidpotentini.repository.metodologia;

import com.github.davidpotentini.model.metodologia.PraticaModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface PraticaRepository extends JpaRepository<PraticaModel, Long> {

    /** Práticas de um processo, na ordem de cadastro. */
    List<PraticaModel> findByPrcCodOrderByPrtCodAsc(Long prcCod);

    /** Práticas dos processos informados (as da versão) — usada para reunir os indicadores da versão. */
    List<PraticaModel> findByPrcCodIn(Collection<Long> prcCods);
}

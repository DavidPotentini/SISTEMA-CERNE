package com.github.davidpotentini.repository.metodologia;

import com.github.davidpotentini.model.metodologia.PraticaModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PraticaRepository extends JpaRepository<PraticaModel, Long> {

    List<PraticaModel> findByPrcCodOrderByOrdemAscPrtCodAsc(Long prcCod);

    Optional<PraticaModel> findFirstByPrcCodOrderByOrdemDesc(Long prcCod);

    List<PraticaModel> findByPrcCodIn(Collection<Long> prcCods);
}

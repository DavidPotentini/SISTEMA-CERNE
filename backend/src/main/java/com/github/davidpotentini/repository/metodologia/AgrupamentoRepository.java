package com.github.davidpotentini.repository.metodologia;

import com.github.davidpotentini.model.metodologia.AgrupamentoModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AgrupamentoRepository extends JpaRepository<AgrupamentoModel, Long> {

    List<AgrupamentoModel> findByPrtCodInOrderByOrdemAscAgrCodAsc(Collection<Long> prtCods);

    List<AgrupamentoModel> findByPrtCodOrderByOrdemAscAgrCodAsc(Long prtCod);

    Optional<AgrupamentoModel> findFirstByPrtCodOrderByOrdemDesc(Long prtCod);
}

package com.github.davidpotentini.repository.estruturaciclo;

import com.github.davidpotentini.model.estruturaciclo.ProcessoCicloModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProcessoCicloRepository extends JpaRepository<ProcessoCicloModel, Long> {

    List<ProcessoCicloModel> findByCicCodOrderByOrdemAscPrccCodAsc(Long cicCod);

    Optional<ProcessoCicloModel> findByCicCodAndPrcCodOrigem(Long cicCod, Long prcCodOrigem);
}

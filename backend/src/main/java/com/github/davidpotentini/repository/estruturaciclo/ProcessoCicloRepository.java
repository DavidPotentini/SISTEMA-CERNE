package com.github.davidpotentini.repository.estruturaciclo;

import com.github.davidpotentini.model.estruturaciclo.ProcessoCicloModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProcessoCicloRepository extends JpaRepository<ProcessoCicloModel, Long> {

    /** Processos (instância) de um ciclo, na ordem de exibição. */
    List<ProcessoCicloModel> findByCicCodOrderByOrdemAscPrccCodAsc(Long cicCod);

    /** Instância de um processo do template dentro do ciclo (para remapear na geração). */
    Optional<ProcessoCicloModel> findByCicCodAndPrcCodOrigem(Long cicCod, Long prcCodOrigem);
}

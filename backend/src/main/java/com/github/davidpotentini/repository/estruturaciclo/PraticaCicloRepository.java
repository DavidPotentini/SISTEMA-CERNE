package com.github.davidpotentini.repository.estruturaciclo;

import com.github.davidpotentini.model.estruturaciclo.PraticaCicloModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PraticaCicloRepository extends JpaRepository<PraticaCicloModel, Long> {

    /** Práticas (instância) de um ciclo, na ordem de cadastro. */
    List<PraticaCicloModel> findByCicCodOrderByPrtcCodAsc(Long cicCod);

    /** Práticas (instância) de um processo do ciclo, na ordem de cadastro. */
    List<PraticaCicloModel> findByPrccCodOrderByPrtcCodAsc(Long prccCod);

    /** Instância de uma prática do template dentro do ciclo (para remapear na geração). */
    Optional<PraticaCicloModel> findByCicCodAndPrtCodOrigem(Long cicCod, Long prtCodOrigem);
}

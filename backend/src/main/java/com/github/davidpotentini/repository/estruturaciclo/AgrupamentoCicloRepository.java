package com.github.davidpotentini.repository.estruturaciclo;

import com.github.davidpotentini.model.estruturaciclo.AgrupamentoCicloModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AgrupamentoCicloRepository extends JpaRepository<AgrupamentoCicloModel, Long> {

    /** Agrupamentos (instância) de um ciclo — usado para remapear template → instância na geração. */
    List<AgrupamentoCicloModel> findByCicCodOrderByAgrcCodAsc(Long cicCod);

    /** Agrupamentos (instância) de uma prática do ciclo, na ordem de exibição ({@code ordem}). */
    List<AgrupamentoCicloModel> findByPrtcCodOrderByOrdemAscAgrcCodAsc(Long prtcCod);

    /** Instância de um agrupamento do template dentro do ciclo (para remapear na geração). */
    Optional<AgrupamentoCicloModel> findByCicCodAndAgrCodOrigem(Long cicCod, Long agrCodOrigem);

    /** Grupo dinâmico "por incubada" de uma prática do ciclo (find-or-create na geração). */
    Optional<AgrupamentoCicloModel> findByCicCodAndPrtcCodAndEmpCod(Long cicCod, Long prtcCod, Long empCod);
}

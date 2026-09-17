package com.github.davidpotentini.repository.estruturaciclo;

import com.github.davidpotentini.model.estruturaciclo.AgrupamentoCicloModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AgrupamentoCicloRepository extends JpaRepository<AgrupamentoCicloModel, Long> {

    List<AgrupamentoCicloModel> findByCicCodOrderByAgrcCodAsc(Long cicCod);

    List<AgrupamentoCicloModel> findByPrtcCodOrderByOrdemAscAgrcCodAsc(Long prtcCod);

    Optional<AgrupamentoCicloModel> findByCicCodAndAgrCodOrigem(Long cicCod, Long agrCodOrigem);

    Optional<AgrupamentoCicloModel> findByCicCodAndPrtcCodAndEmpCod(Long cicCod, Long prtcCod, Long empCod);
}

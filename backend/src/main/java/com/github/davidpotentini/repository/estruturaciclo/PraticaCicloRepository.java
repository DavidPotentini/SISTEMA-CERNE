package com.github.davidpotentini.repository.estruturaciclo;

import com.github.davidpotentini.model.estruturaciclo.PraticaCicloModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PraticaCicloRepository extends JpaRepository<PraticaCicloModel, Long> {

    List<PraticaCicloModel> findByCicCodOrderByPrtcCodAsc(Long cicCod);

    List<PraticaCicloModel> findByPrccCodOrderByOrdemAscPrtcCodAsc(Long prccCod);

    Optional<PraticaCicloModel> findByCicCodAndPrtCodOrigem(Long cicCod, Long prtCodOrigem);
}

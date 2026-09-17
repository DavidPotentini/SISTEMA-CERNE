package com.github.davidpotentini.repository.indicador;

import com.github.davidpotentini.enums.EOrigemIndicador;
import com.github.davidpotentini.model.indicador.IndicadorModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IndicadorRepository extends JpaRepository<IndicadorModel, Long> {

    List<IndicadorModel> findByCicCodOrderByNomeAsc(Long cicCod);

    void deleteByCicCodAndOrigem(Long cicCod, EOrigemIndicador origem);

    boolean existsByCicCodAndOrigem(Long cicCod, EOrigemIndicador origem);

    boolean existsByCicCodAndRespPesCodNotNull(Long cicCod);
}

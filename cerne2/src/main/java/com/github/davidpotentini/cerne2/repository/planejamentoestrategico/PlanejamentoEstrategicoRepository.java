package com.github.davidpotentini.cerne2.repository.planejamentoestrategico;

import com.github.davidpotentini.cerne2.models.planejamentoestrategico.PlanejamentoEstrategicoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PlanejamentoEstrategicoRepository extends JpaRepository<PlanejamentoEstrategicoModel, Long> {
    List<PlanejamentoEstrategicoModel> findByIncubadasModel_IncCod(Long incCod);

    @Query(value = "SELECT * FROM PLANEJAMENTO_ESTRATEGICO WHERE VLD_TIPO_EMPREENDIMENTO = 'INCUBADORA' AND INC_COD IS NULL", nativeQuery = true)
    List<PlanejamentoEstrategicoModel> findByIncubadora();
}

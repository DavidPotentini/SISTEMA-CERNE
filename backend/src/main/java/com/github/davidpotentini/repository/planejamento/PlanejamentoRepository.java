package com.github.davidpotentini.repository.planejamento;

import com.github.davidpotentini.enums.EStatusPlanejamento;
import com.github.davidpotentini.model.planejamento.PlanejamentoModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlanejamentoRepository extends JpaRepository<PlanejamentoModel, Long> {

    Optional<PlanejamentoModel> findByCicCodAndStatus(Long cicCod, EStatusPlanejamento status);
}

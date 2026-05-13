package com.github.davidpotentini.cerne2.repository.metricas;

import com.github.davidpotentini.cerne2.models.metricas.IndicadoresMetricasModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IndicadoresMetricasRepository extends JpaRepository<IndicadoresMetricasModel, Long> {
    Optional<List<IndicadoresMetricasModel>> findByMetricasModel_MetCod(Long metCod);
}

package com.github.davidpotentini.cerne2.repository.metricas;

import com.github.davidpotentini.cerne2.models.metricas.QuantidadeMensalMetricasModel;
import com.github.davidpotentini.cerne2.models.metricas.ids.QuantidadeMensalMetricasId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuantidadeMensalMetricasRepository extends JpaRepository<QuantidadeMensalMetricasModel, QuantidadeMensalMetricasId> {

    void deleteByIndicadoresMetricasModel_IndCod(Long indCod);
}

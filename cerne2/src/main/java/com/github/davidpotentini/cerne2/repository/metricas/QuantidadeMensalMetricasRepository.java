package com.github.davidpotentini.cerne2.repository.metricas;

import com.github.davidpotentini.cerne2.models.metricas.QuantidadeMensalMetricasModel;
import com.github.davidpotentini.cerne2.models.metricas.ids.QuantidadeMensalMetricasId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuantidadeMensalMetricasRepository extends JpaRepository<QuantidadeMensalMetricasModel, QuantidadeMensalMetricasId> {

    @Query(value = "DELETE FROM QUANTIDADE_MENSAL_METRICAS WHERE IND_COD = :IND_COD", nativeQuery = true)
    void deleteAllByIndCod(@Param("IND_COD") Long indCod);
}

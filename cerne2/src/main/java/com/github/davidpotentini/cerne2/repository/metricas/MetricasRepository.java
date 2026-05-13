package com.github.davidpotentini.cerne2.repository.metricas;

import com.github.davidpotentini.cerne2.models.metricas.MetricasModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MetricasRepository extends JpaRepository<MetricasModel, Long> {
}

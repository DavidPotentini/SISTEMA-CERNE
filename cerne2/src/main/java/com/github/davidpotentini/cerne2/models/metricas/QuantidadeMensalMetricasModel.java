package com.github.davidpotentini.cerne2.models.metricas;

import com.github.davidpotentini.cerne2.models.metricas.ids.QuantidadeMensalMetricasId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity(name = "QUANTIDADE_MENSAL_METRICAS")
@Table
@Getter
@Setter
public class QuantidadeMensalMetricasModel {
    @EmbeddedId
    private QuantidadeMensalMetricasId quantidadeMensalMetricasId;

    @Column(name = "QUANTIDADE")
    private BigDecimal quantidade;

    @JoinColumn(name = "IND_COD")
    @MapsId("indCod")
    @ManyToOne
    private IndicadoresMetricasModel indicadoresMetricasModel;
}

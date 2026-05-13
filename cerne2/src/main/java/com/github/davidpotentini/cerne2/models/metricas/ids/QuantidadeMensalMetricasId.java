package com.github.davidpotentini.cerne2.models.metricas.ids;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
@Getter
@Setter
public class QuantidadeMensalMetricasId implements Serializable {

    @Column(name = "IND_COD")
    private Long indCod;

    @Column(name = "MES_COD")
    private Short mesCod;
}

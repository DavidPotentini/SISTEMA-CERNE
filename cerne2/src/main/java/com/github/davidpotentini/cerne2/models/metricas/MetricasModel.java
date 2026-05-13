package com.github.davidpotentini.cerne2.models.metricas;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity(name = "METRICAS")
@Table
@Getter
@Setter
public class MetricasModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MET_COD")
    private Long metCod;

    @Column(name = "DESCRICAO")
    private String descricao;

    @OneToMany(mappedBy = "metricasModel")
    private List<IndicadoresMetricasModel> indicadoresMetricasModelList;
}

package com.github.davidpotentini.cerne2.models.metricas;

import com.github.davidpotentini.cerne2.models.planejamentoestrategico.ObjetivosModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity(name = "INDICADORES_METRICAS")
@Table
@Getter
@Setter
public class IndicadoresMetricasModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IND_COD")
    private Long indCod;

    @Column(name = "DESCRICAO")
    private String descricao;

    @Column(name = "META")
    private BigDecimal meta;

    @JoinColumn(name = "OBJ_COD")
    @ManyToOne
    private ObjetivosModel objetivosModel;

    @JoinColumn(name = "MET_COD")
    @ManyToOne
    private MetricasModel metricasModel;

    @OneToMany(mappedBy = "indicadoresMetricasModel")
    private List<QuantidadeMensalMetricasModel> quantidadeMensalMetricasModelList;
}

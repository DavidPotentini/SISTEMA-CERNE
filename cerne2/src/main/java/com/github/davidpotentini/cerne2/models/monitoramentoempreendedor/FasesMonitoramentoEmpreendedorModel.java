package com.github.davidpotentini.cerne2.models.monitoramentoempreendedor;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity(name = "FASES_MONITORAMENTO_EMPREENDEDOR")
@Table
@Getter
@Setter
public class FasesMonitoramentoEmpreendedorModel {

    @Column(name = "FAS_COD")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fasCod;

    @Column(name = "DESCRICAO")
    private String descricao;

    @JoinColumn(name = "MON_COD")
    @ManyToOne
    private MonitoramentoEmpreendedorModel monitoramentoEmpreendedorModel;

    @OneToMany(mappedBy = "fasesMonitoramentoEmpreendedorModel")
    private List<PerguntasMonitoramentoEmpreendedorModel> perguntasMonitoramentoEmpreendedorModelList;
}

package com.github.davidpotentini.cerne2.models.monitoramentoempreendedor;

import com.github.davidpotentini.cerne2.models.pessoas.PessoasModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Entity (name = "MONITORAMENTO_EMPREENDEDOR")
@Table
@Getter
@Setter
public class MonitoramentoEmpreendedorModel {

    @Column(name = "MON_COD")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long monCod;

    @Column(name = "DESCRICAO")
    private String descricao;

    @JoinColumn(name = "PES_COD")
    @OneToOne
    private PessoasModel pessoasModel;

    @OneToMany(mappedBy = "monitoramentoEmpreendedorModel")
    private List<FasesMonitoramentoEmpreendedorModel> fasesMonitoramentoEmpreendedorModelList;
}

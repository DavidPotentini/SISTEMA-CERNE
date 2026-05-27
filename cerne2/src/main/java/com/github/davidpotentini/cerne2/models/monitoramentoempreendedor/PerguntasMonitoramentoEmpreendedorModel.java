package com.github.davidpotentini.cerne2.models.monitoramentoempreendedor;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.mapstruct.control.DeepClone;

@Entity(name = "PERGUNTAS_MONITORAMENTO_EMPREENDEDOR")
@Table
@Getter
@Setter
public class PerguntasMonitoramentoEmpreendedorModel {

    @Column(name = "PERG_COD")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pergCod;

    @Column(name = "DESCRICAO_PERGUNTA")
    private String descricaoPergunta;

    @Column(name = "PONTUACAO")
    private Integer pontuacao;

    @Column(name = "OBSERVACAO")
    private String observacao;

    @JoinColumn(name = "FAS_COD")
    @ManyToOne
    private FasesMonitoramentoEmpreendedorModel fasesMonitoramentoEmpreendedorModel;
}

package com.github.davidpotentini.cerne2.models.planejamentoestrategico;


import com.github.davidpotentini.cerne2.enums.ESituacaoTarefa;
import com.github.davidpotentini.cerne2.models.pessoas.PessoasModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.List;

@Entity (name = "TAREFAS")
@Table
@Getter
@Setter
public class TarefasModel {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "TRF_COD")
    private Long trfCod;

    @Column (name = "NOME")
    private String nome;

    @Column (name = "DATA_INICIO")
    private LocalDate dataInicio;

    @Column (name = "DATA_TERMINO")
    private LocalDate dataTermino;

    /*UTILIZADO SOMENTE NO MONITORAMENTO DE PLANEJAMENTO*/
    @Column (name = "PONTUACAO")
    private Integer pontuacao;

    /*UTILIZADO SOMENTE NO MONITORAMENTO DE PLANEJAMENTO*/
    @Column (name = "OBSERVACAO")
    private String observacao;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column (name = "SITUACAO_TAREFA")
    private ESituacaoTarefa eSituacaoTarefa;

    @JoinColumn (name = "OBJ_COD")
    @ManyToOne
    private ObjetivosModel objetivosModel;

    @JoinColumn (name = "RESP_COD")
    @ManyToOne
    private PessoasModel pessoasModel;

//    @OneToMany (mappedBy = "tarefas")
//    private List<AgrupamentosTarefasModel> tarefasList;
//
    @OneToMany (mappedBy = "tarefasModel")
    private List<EvidenciasModel> evidenciasModelList;
}

package com.github.davidpotentini.cerne2.models.planejamentoestrategico;


import com.github.davidpotentini.cerne2.enums.ESituacaoTarefa;
import com.github.davidpotentini.cerne2.models.pessoas.PessoasModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

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

    @Enumerated(EnumType.STRING)
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
//    @OneToMany (mappedBy = "tarefas")
//    private List<EvidenciasModel> evidenciasModelList;
}

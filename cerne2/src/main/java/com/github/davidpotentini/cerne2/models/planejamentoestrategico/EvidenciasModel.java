package com.github.davidpotentini.cerne2.models.planejamentoestrategico;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "EVIDENCIAS")
@Table
@Getter
@Setter
public class EvidenciasModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EVD_COD")
    private Long evdCod;

    @Column (name = "DESCRICAO")
    private String descricao;

    @Column (name = "CAMINHO_ARQUIVO")
    private String caminhoArquivo;

    @ManyToOne
    @JoinColumn (name = "TRF_COD")
    private TarefasModel tarefasModel;

}

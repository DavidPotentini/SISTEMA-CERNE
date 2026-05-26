package com.github.davidpotentini.cerne2.models.planejamentoestrategico;

import com.github.davidpotentini.cerne2.models.arquivo.ArquivosEvidenciasModel;
import com.github.davidpotentini.cerne2.models.arquivo.ArquivosIncubadasModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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

    @ManyToOne
    @JoinColumn (name = "TRF_COD")
    private TarefasModel tarefasModel;

    @OneToMany(mappedBy = "evidenciasModel")
    private List<ArquivosEvidenciasModel> arquivosEvidenciasModelList;
}

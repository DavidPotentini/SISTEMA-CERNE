package com.github.davidpotentini.cerne2.models.planejamentoestrategico;


import com.github.davidpotentini.cerne2.models.metricas.IndicadoresMetricasModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity(name = "OBJETIVOS")
@Table
@Getter
@Setter
public class ObjetivosModel {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column(name = "OBJ_COD")
    private Long objCod;

    @Column(name = "NOME")
    private String nome;

    @Column(name = "DATA_INICIO")
    private LocalDate dataInicio;

    @Column(name = "DATA_TERMINO")
    private LocalDate dataTermino;

    @JoinColumn(name = "PRJ_COD")
    @ManyToOne
    private ProjetosModel projetosModel;

    @OneToMany (mappedBy = "objetivosModel")
    private List<TarefasModel> tarefasModelList;

    @OneToMany (mappedBy = "objetivosModel")
    private List<IndicadoresMetricasModel> indicadoresMetricasModelList;
}

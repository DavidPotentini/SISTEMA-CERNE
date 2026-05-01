package com.github.davidpotentini.cerne2.models.planejamentoestrategico;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity (name = "PROJETOS")
@Table
@Getter
@Setter
public class ProjetosModel {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "PRJ_COD")
    private Long prjCod;

    @Column (name = "NOME")
    private String nome;

    @Column (name = "DATA_INICIO")
    private LocalDate dataInicio;

    @Column (name = "DATA_TERMINO")
    private LocalDate dataTermino;

    @JoinColumn (name = "PES_COD")
    @ManyToOne
    private PlanejamentoEstrategicoModel planejamentoEstrategicoModel;

    @OneToMany (mappedBy = "projetosModel")
    private List<ObjetivosModel> objetivosModelList;
}

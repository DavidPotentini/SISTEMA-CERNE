package com.github.davidpotentini.cerne2.models.planejamentoestrategico;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity(name = "PLANEJAMENTO_ESTRATEGICO")
@Table
@Getter
@Setter
public class PlanejamentoEstrategicoModel {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column(name = "PES_COD")
    private Long pesCod;

    @Column(name = "NOME")
    private String nome;

    @Column(name = "DATA_INICIO")
    private LocalDate dataInicio;

    @Column(name = "DATA_TERMINO")
    private LocalDate dataTermino;

    @OneToMany(mappedBy = "planejamentoEstrategicoModel")
    private List<ProjetosModel> projetosModelList;
}

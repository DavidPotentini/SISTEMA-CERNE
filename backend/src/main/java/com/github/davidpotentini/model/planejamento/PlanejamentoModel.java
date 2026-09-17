package com.github.davidpotentini.model.planejamento;

import com.github.davidpotentini.enums.EStatusPlanejamento;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "PLANEJAMENTOS")
@Getter
@Setter
public class PlanejamentoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PLN_COD")
    private Long plnCod;

    @Column(name = "NOME", nullable = false)
    private String nome;

    @Column(name = "CIC_COD", nullable = false)
    private Long cicCod;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private EStatusPlanejamento status = EStatusPlanejamento.PUBLICADO;

    @Column(name = "INICIO")
    private LocalDate inicio;

    @Column(name = "FIM")
    private LocalDate fim;

    @Column(name = "RESP_PES_COD")
    private Long respPesCod;
}

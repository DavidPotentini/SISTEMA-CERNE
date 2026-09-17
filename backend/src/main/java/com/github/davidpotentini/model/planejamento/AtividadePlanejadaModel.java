package com.github.davidpotentini.model.planejamento;

import com.github.davidpotentini.enums.EOrigemAtividade;
import com.github.davidpotentini.enums.EStatusAtividade;
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
@Table(name = "ATIVIDADES_PLANEJADAS")
@Getter
@Setter
public class AtividadePlanejadaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ATP_COD")
    private Long atpCod;

    @Column(name = "PLN_COD", nullable = false)
    private Long plnCod;

    @Enumerated(EnumType.STRING)
    @Column(name = "ORIGEM", nullable = false)
    private EOrigemAtividade origem = EOrigemAtividade.METODOLOGIA;

    @Column(name = "PRTC_COD", nullable = false)
    private Long prtcCod;

    @Column(name = "AGRC_COD")
    private Long agrcCod;

    @Column(name = "ORDEM", nullable = false)
    private Integer ordem;

    @Column(name = "NOME", nullable = false)
    private String nome;

    @Column(name = "OBSERVACOES")
    private String observacoes;

    @Column(name = "RESP_PES_COD")
    private Long respPesCod;

    @Column(name = "PRAZO")
    private LocalDate prazo;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private EStatusAtividade status = EStatusAtividade.PLANEJADA;

    @Column(name = "EMP_COD")
    private Long empCod;
}

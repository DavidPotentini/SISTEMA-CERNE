package com.github.davidpotentini.model.monitoramento;

import com.github.davidpotentini.enums.ERecomendacaoMonitor;
import com.github.davidpotentini.enums.EStatusMonitoramento;
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
@Table(name = "AVALIACOES_MONITORAMENTO")
@Getter
@Setter
public class AvaliacaoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AVA_COD")
    private Long avaCod;

    @Column(name = "ROD_COD", nullable = false)
    private Long rodCod;

    @Column(name = "EMP_COD", nullable = false)
    private Long empCod;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private EStatusMonitoramento status = EStatusMonitoramento.EM_ANDAMENTO;

    @Column(name = "DATA")
    private LocalDate data;

    @Enumerated(EnumType.STRING)
    @Column(name = "RECOMENDACAO")
    private ERecomendacaoMonitor recomendacao;

    @Column(name = "OBSERVACAO")
    private String observacao;
}

package com.github.davidpotentini.model.monitoramento;

import com.github.davidpotentini.enums.ESituacaoRodada;
import com.github.davidpotentini.enums.ETipoRodada;
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
@Table(name = "RODADAS_MONITORAMENTO")
@Getter
@Setter
public class RodadaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ROD_COD")
    private Long rodCod;

    @Column(name = "NOME", nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO", nullable = false)
    private ETipoRodada tipo = ETipoRodada.PERIODICO;

    @Column(name = "CIC_COD")
    private Long cicCod;

    @Column(name = "RESP_PES_COD")
    private Long respPesCod;

    @Column(name = "PRAZO")
    private LocalDate prazo;

    @Enumerated(EnumType.STRING)
    @Column(name = "SITUACAO", nullable = false)
    private ESituacaoRodada situacao = ESituacaoRodada.EM_ANDAMENTO;
}

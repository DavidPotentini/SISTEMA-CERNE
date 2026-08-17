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

/**
 * Rodada de monitoramento das incubadas — schema do tenant. Aplica um instrumento de avaliação a um
 * conjunto de empreendimentos (participação em {@code RODADA_INCUBADAS}). Nasce {@code EM_ANDAMENTO}
 * e é encerrada ({@code CONCLUIDA}) quando as avaliações terminam. {@code cicCod} é o ciclo ativo no
 * momento do planejamento; {@code respPesCod} é o responsável interno da rodada ({@code PESSOAS}).
 */
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

    /** Responsável da rodada: pessoa da equipe da incubadora ({@code PESSOAS}, ref. via {@code PES_COD}). */
    @Column(name = "RESP_PES_COD")
    private Long respPesCod;

    @Column(name = "PRAZO")
    private LocalDate prazo;

    @Enumerated(EnumType.STRING)
    @Column(name = "SITUACAO", nullable = false)
    private ESituacaoRodada situacao = ESituacaoRodada.EM_ANDAMENTO;
}

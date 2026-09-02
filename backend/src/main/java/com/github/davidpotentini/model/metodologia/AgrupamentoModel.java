package com.github.davidpotentini.model.metodologia;

import com.github.davidpotentini.enums.EAtivoInativo;
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

/**
 * Agrupamento (sub-plano do Manual) de uma {@link PraticaModel} ({@code PRT_COD}) — reúne as atividades
 * de uma prática sob um título ordenado (ex.: em "Sensibilização": eventos externos, internos, rádio/TV).
 * É o nível entre prática e atividade na árvore. A atividade referencia {@code AGR_COD} (opcional).
 * Schema do tenant.
 */
@Entity
@Table(name = "AGRUPAMENTOS")
@Getter
@Setter
public class AgrupamentoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AGR_COD")
    private Long agrCod;

    @Column(name = "PRT_COD", nullable = false)
    private Long prtCod;

    /** Ordem do agrupamento dentro da prática (sequência de exibição). */
    @Column(name = "ORDEM", nullable = false)
    private Integer ordem;

    @Column(name = "NOME", nullable = false)
    private String nome;

    @Column(name = "DESCRICAO")
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(name = "SITUACAO", nullable = false)
    private EAtivoInativo situacao = EAtivoInativo.ATIVO;
}

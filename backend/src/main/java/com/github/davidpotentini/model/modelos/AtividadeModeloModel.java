package com.github.davidpotentini.model.modelos;

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
 * Atividade de um {@link ModeloModel} ({@code MOD_COD}), pendurada numa prática ({@code PRT_COD},
 * obrigatório; o processo deriva da prática). Vários por prática. É o único nível editável na tela de
 * modelos — pode ser desativada (continua visível, atenuada). Schema do tenant.
 */
@Entity
@Table(name = "ATIVIDADES_MODELO")
@Getter
@Setter
public class AtividadeModeloModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ATM_COD")
    private Long atmCod;

    @Column(name = "MOD_COD", nullable = false)
    private Long modCod;

    @Column(name = "PRT_COD", nullable = false)
    private Long prtCod;

    @Column(name = "NOME", nullable = false)
    private String nome;

    @Column(name = "DESCRICAO")
    private String descricao;

    /** Responsável padrão → {@code PESSOAS(PES_COD)}. Opcional; guardado como código. */
    @Column(name = "RESP_PES_COD")
    private Long respPesCod;

    @Enumerated(EnumType.STRING)
    @Column(name = "SITUACAO", nullable = false)
    private EAtivoInativo situacao = EAtivoInativo.ATIVO;
}

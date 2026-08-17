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
 * Prática de um {@link ProcessoModel} ({@code PRC_COD}). Aparece dentro do accordion do processo na
 * aba "Processos e Práticas". Herda a versão pela cadeia (não carrega {@code VER_COD}). Schema do tenant.
 */
@Entity
@Table(name = "PRATICAS")
@Getter
@Setter
public class PraticaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRT_COD")
    private Long prtCod;

    @Column(name = "PRC_COD", nullable = false)
    private Long prcCod;

    @Column(name = "NOME", nullable = false)
    private String nome;

    @Column(name = "DESCRICAO")
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(name = "SITUACAO", nullable = false)
    private EAtivoInativo situacao = EAtivoInativo.ATIVO;
}

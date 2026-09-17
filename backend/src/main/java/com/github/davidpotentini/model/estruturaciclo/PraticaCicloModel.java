package com.github.davidpotentini.model.estruturaciclo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Instância própria da prática dentro de um ciclo (chave {@code PRTC_COD}, que os filhos do ciclo
 * referenciam). {@code PRT_COD_ORIGEM} é proveniência fraca (sem FK), lida só na geração.
 */
@Entity
@Table(name = "PRATICAS_CICLO")
@Getter
@Setter
public class PraticaCicloModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRTC_COD")
    private Long prtcCod;

    @Column(name = "CIC_COD", nullable = false)
    private Long cicCod;

    @Column(name = "PRCC_COD", nullable = false)
    private Long prccCod;

    @Column(name = "PRT_COD_ORIGEM")
    private Long prtCodOrigem;

    @Column(name = "ORDEM", nullable = false)
    private Integer ordem;

    @Column(name = "NOME", nullable = false)
    private String nome;

    @Column(name = "DESCRICAO")
    private String descricao;
}

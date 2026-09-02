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
 * Instância de uma prática da metodologia dentro de um ciclo — cópia com chave própria
 * ({@code PRTC_COD}), pendurada num {@link ProcessoCicloModel} do mesmo ciclo ({@code PRCC_COD}). É a
 * chave que os filhos do ciclo referenciam (atividades planejadas, indicadores). {@code PRT_COD_ORIGEM}
 * é proveniência fraca (sem FK), lida só na geração. Schema do tenant.
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

    /** Prática do template de origem — proveniência fraca (sem FK forte). */
    @Column(name = "PRT_COD_ORIGEM")
    private Long prtCodOrigem;

    /** Ordem da prática dentro do processo do ciclo (copiada do template). */
    @Column(name = "ORDEM", nullable = false)
    private Integer ordem;

    @Column(name = "NOME", nullable = false)
    private String nome;

    @Column(name = "DESCRICAO")
    private String descricao;
}

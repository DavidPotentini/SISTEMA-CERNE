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
 * Instância própria do agrupamento dentro de um ciclo (chave {@code AGRC_COD}, que as atividades
 * planejadas referenciam). {@code AGR_COD_ORIGEM} é proveniência fraca (sem FK), lida só na geração.
 */
@Entity
@Table(name = "AGRUPAMENTOS_CICLO")
@Getter
@Setter
public class AgrupamentoCicloModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AGRC_COD")
    private Long agrcCod;

    @Column(name = "CIC_COD", nullable = false)
    private Long cicCod;

    @Column(name = "PRTC_COD", nullable = false)
    private Long prtcCod;

    @Column(name = "AGR_COD_ORIGEM")
    private Long agrCodOrigem;

    /** Empreendimento do grupo dinâmico "por incubada"; nulo nos grupos vindos do template. */
    @Column(name = "EMP_COD")
    private Long empCod;

    @Column(name = "ORDEM", nullable = false)
    private Integer ordem;

    @Column(name = "NOME", nullable = false)
    private String nome;

    @Column(name = "DESCRICAO")
    private String descricao;
}

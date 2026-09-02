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
 * Instância de um agrupamento (sub-plano) da metodologia dentro de um ciclo — cópia com chave própria
 * ({@code AGRC_COD}), pendurada numa {@link PraticaCicloModel} do mesmo ciclo ({@code PRTC_COD}). É a
 * chave que as atividades planejadas referenciam ({@code AGRC_COD}). {@code AGR_COD_ORIGEM} é
 * proveniência fraca (sem FK), lida só na geração. Schema do tenant.
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

    /** Agrupamento do template de origem — proveniência fraca (sem FK forte). */
    @Column(name = "AGR_COD_ORIGEM")
    private Long agrCodOrigem;

    /** Empreendimento do grupo dinâmico "por incubada" → {@code EMPREENDIMENTOS(EMP_COD)}. Nulo nos do template. */
    @Column(name = "EMP_COD")
    private Long empCod;

    /** Ordem do agrupamento dentro da prática do ciclo (copiada do template). */
    @Column(name = "ORDEM", nullable = false)
    private Integer ordem;

    @Column(name = "NOME", nullable = false)
    private String nome;

    @Column(name = "DESCRICAO")
    private String descricao;
}

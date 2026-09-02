package com.github.davidpotentini.model.estruturaciclo;

import com.github.davidpotentini.enums.ENivelCerne;
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
 * Instância de um processo da metodologia dentro de um ciclo ({@code CIC_COD}) — cópia com chave
 * própria ({@code PRCC_COD}), criada na abertura do ciclo. O ciclo passa a ser dono da sua estrutura:
 * exclusão/inativação no template não o alteram. {@code PRC_COD_ORIGEM} é proveniência fraca (sem FK),
 * lida só na geração para remapear template → instância. Schema do tenant.
 */
@Entity
@Table(name = "PROCESSOS_CICLO")
@Getter
@Setter
public class ProcessoCicloModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRCC_COD")
    private Long prccCod;

    @Column(name = "CIC_COD", nullable = false)
    private Long cicCod;

    /** Processo do template de origem — proveniência fraca (sem FK forte). */
    @Column(name = "PRC_COD_ORIGEM")
    private Long prcCodOrigem;

    /** Nível CERNE do processo — por ora sempre {@code CERNE_1} (domínio de um valor só). */
    @Enumerated(EnumType.STRING)
    @Column(name = "NIVEL", nullable = false)
    private ENivelCerne nivel = ENivelCerne.CERNE_1;

    @Column(name = "ORDEM", nullable = false)
    private Integer ordem;

    @Column(name = "NOME", nullable = false)
    private String nome;

    @Column(name = "DESCRICAO")
    private String descricao;
}

package com.github.davidpotentini.model.metodologia;

import com.github.davidpotentini.enums.EAtivoInativo;
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
 * Processo da metodologia (documento vivo da incubadora) — agrupa práticas. Exibido como accordion na
 * aba "Processos e Práticas", ordenado por {@code ordem} ({@code ORDEM} é UNIQUE no banco). Schema do
 * tenant.
 */
@Entity
@Table(name = "PROCESSOS_METODOLOGIA")
@Getter
@Setter
public class ProcessoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRC_COD")
    private Long prcCod;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "SITUACAO", nullable = false)
    private EAtivoInativo situacao = EAtivoInativo.ATIVO;
}

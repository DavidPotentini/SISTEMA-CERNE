package com.github.davidpotentini.model.planejamento;

import com.github.davidpotentini.enums.EOrigemAtividade;
import com.github.davidpotentini.enums.EStatusAtividade;
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
 * Atividade de um {@link PlanejamentoModel} ({@code PLN_COD}), pendurada numa prática ({@code PRT_COD},
 * obrigatório). {@code origem} distingue as copiadas do modelo ({@code MODELO}) das incluídas à mão
 * ({@code COMPLEMENTAR}) — ambas podem ser ajustadas. {@code status} é o estado de execução (o
 * progresso do plano é a fração {@code CONCLUIDA}). Schema do tenant.
 */
@Entity
@Table(name = "ATIVIDADES_PLANEJADAS")
@Getter
@Setter
public class AtividadePlanejadaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ATP_COD")
    private Long atpCod;

    @Column(name = "PLN_COD", nullable = false)
    private Long plnCod;

    @Enumerated(EnumType.STRING)
    @Column(name = "ORIGEM", nullable = false)
    private EOrigemAtividade origem = EOrigemAtividade.MODELO;

    @Column(name = "PRT_COD", nullable = false)
    private Long prtCod;

    @Column(name = "NOME", nullable = false)
    private String nome;

    @Column(name = "OBSERVACOES")
    private String observacoes;

    /** Responsável da atividade → {@code PESSOAS(PES_COD)}. Opcional; guardado como código. */
    @Column(name = "RESP_PES_COD")
    private Long respPesCod;

    @Column(name = "PRAZO")
    private LocalDate prazo;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private EStatusAtividade status = EStatusAtividade.PLANEJADA;

    /** Empreendimento referenciado (opcional) → {@code EMPREENDIMENTOS(EMP_COD)}. */
    @Column(name = "EMP_COD")
    private Long empCod;
}

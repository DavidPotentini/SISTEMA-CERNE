package com.github.davidpotentini.model.modelos;

import com.github.davidpotentini.enums.EPeriodicidade;
import com.github.davidpotentini.enums.EStatusModelo;
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

import java.time.LocalDateTime;

/**
 * Modelo de planejamento — baseado numa versão da metodologia ({@code VER_COD}, a VIGENTE no momento
 * da criação). A estrutura de processos/práticas é herdada dessa versão (não é copiada); o que o
 * modelo guarda são as {@link AtividadeModeloModel atividades} de cada prática.
 *
 * <p>Enquanto {@code RASCUNHO} as atividades podem ser editadas; ao {@code PUBLICADO} o modelo fica
 * imutável. Schema do tenant.
 */
@Entity
@Table(name = "MODELOS")
@Getter
@Setter
public class ModeloModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MOD_COD")
    private Long modCod;

    /** Versão da metodologia base (a VIGENTE no momento da criação). */
    @Column(name = "VER_COD", nullable = false)
    private Long verCod;

    @Column(name = "NOME", nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "PERIODICIDADE")
    private EPeriodicidade periodicidade = EPeriodicidade.ANUAL;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private EStatusModelo status = EStatusModelo.RASCUNHO;

    /** Preenchido na publicação; {@code null} enquanto rascunho. */
    @Column(name = "PUBLICADO_EM")
    private LocalDateTime publicadoEm;

    @Column(name = "DESCRICAO")
    private String descricao;
}

package com.github.davidpotentini.model.metodologia;

import com.github.davidpotentini.enums.EAtivoInativo;
import com.github.davidpotentini.enums.EPeriodicidade;
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
 * Indicador da metodologia — pertence a uma {@link PraticaModel} ({@code PRT_COD}). Schema do tenant.
 */
@Entity
@Table(name = "INDICADORES_METODOLOGIA")
@Getter
@Setter
public class IndicadorMetodologiaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "INM_COD")
    private Long inmCod;

    @Column(name = "PRT_COD", nullable = false)
    private Long prtCod;

    @Column(name = "NOME", nullable = false)
    private String nome;

    @Column(name = "UNIDADE")
    private String unidade;

    @Enumerated(EnumType.STRING)
    @Column(name = "PERIODICIDADE")
    private EPeriodicidade periodicidade = EPeriodicidade.NAO_SE_APLICA;

    @Enumerated(EnumType.STRING)
    @Column(name = "SITUACAO", nullable = false)
    private EAtivoInativo situacao = EAtivoInativo.ATIVO;
}

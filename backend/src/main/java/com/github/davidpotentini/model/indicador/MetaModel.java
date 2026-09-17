package com.github.davidpotentini.model.indicador;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "INDICADOR_METAS")
@Getter
@Setter
public class MetaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MET_COD")
    private Long metCod;

    @Column(name = "IND_COD", nullable = false)
    private Long indCod;

    @Column(name = "VALOR")
    private BigDecimal valor;

    @Column(name = "DATA_INICIO_APURACAO")
    private LocalDate dataInicioApuracao;

    @Column(name = "DATA_FIM_APURACAO")
    private LocalDate dataFimApuracao;
}

package com.github.davidpotentini.model.indicador;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Resultado apurado de uma meta — 1:1 com ela: a chave é o próprio {@code MET_COD} (não gerado). */
@Entity
@Table(name = "INDICADOR_RESULTADOS")
@Getter
@Setter
public class ResultadoModel {

    @Id
    @Column(name = "MET_COD")
    private Long metCod;

    @Column(name = "VALOR")
    private BigDecimal valor;

    @Column(name = "REG_PES_COD")
    private Long regPesCod;

    @Column(name = "DATA_REGISTRO")
    private LocalDateTime dataRegistro;
}

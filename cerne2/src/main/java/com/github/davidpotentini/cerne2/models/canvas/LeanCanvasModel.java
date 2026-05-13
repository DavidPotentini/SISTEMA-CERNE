package com.github.davidpotentini.cerne2.models.canvas;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Entity(name = "LEAN_CANVAS")
@Table
@Getter
@Setter
public class LeanCanvasModel {

    @Id
    private Long ambcCod;

    @Column(name = "PROBLEMA")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> problema;

    @Column(name = "SOLUCAO")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> solucao;

    @Column(name = "METRICA")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> metrica;

    @Column(name = "PROPOSTA_VALOR")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> propostaValor;

    @Column(name = "COMPETENCIA_ESSENCIAL")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> competenciaEssencial;

    @Column(name = "CANAIS")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> canais;

    @Column(name = "SEGMENTO_CLIENTE")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> segmentoCliente;

    @Column(name = "ESTRUTURA_CUSTOS")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> estruturaCustos;

    @Column(name = "MODELO_RECEITA")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> modeloReceita;

    @JoinColumn(name = "AMBC_COD")
    @MapsId("ambcCod")
    @OneToOne
    private AmbienteCanvasModel ambienteCanvasModel;
}

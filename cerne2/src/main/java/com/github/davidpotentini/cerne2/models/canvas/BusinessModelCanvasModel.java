package com.github.davidpotentini.cerne2.models.canvas;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Entity(name = "BUSINESS_MODEL_CANVAS")
@Table
@Getter
@Setter
public class BusinessModelCanvasModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BMC_COD")
    private Long bmcCod;

    @Column(name = "PARCEIROS_CHAVE")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> parceirosChave;

    @Column(name = "ATIVIDADES_CHAVE")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> atividadesChave;

    @Column(name = "PROPOSTAS_VALOR")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> propostasValor;

    @Column(name = "RELACIONAMENTO_CLIENTE")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> relacionamentoCliente;

    @Column(name = "SEGMENTOS_CLIENTE")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> segmentoCliente;

    @Column(name = "CANAIS")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> canais;

    @Column(name = "RECURSOS_CHAVE")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> recursosChave;

    @Column(name = "ESTRUTURA_CUSTOS")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> estruturaCustos;

    @Column(name = "FONTES_RECEITA")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> fontesReceitas;

    @ManyToOne(optional = false)
    @JoinColumn(name = "AMBC_COD")
    private AmbienteCanvasModel ambienteCanvasModel;
}

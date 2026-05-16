package com.github.davidpotentini.cerne2.models.canvas;

import com.github.davidpotentini.cerne2.models.canvas.ids.ChannelImplementationCanvasId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.lang.Object;
import java.util.Map;

@Entity(name = "CHANNEL_IMPLEMENTATION_CANVAS")
@Table
@Getter
@Setter
public class ChannelImplementationCanvasModel {

    @Id
    @Column(name = "SEG_COD")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long segCod;

//    @EmbeddedId
//    private ChannelImplementationCanvasId channelImplementationCanvasId;

    @Column(name = "NOME_SEGMENTO")
    private String nomeSegmento;

    @Column(name = "ATIVIDADE_CONHECIMENTO")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> atividadeConhecimento;

    @Column(name = "ATIVIDADE_AVALIACAO")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> atividadeAvaliacao;

    @Column(name = "ATIVIDADE_COMPRA")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> atividadeCompra;

    @Column(name = "ATIVIDADES_ENTREGA")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> atividadeEntrega;

    @Column(name = "ATIVIDADES_POS_VENDA")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> atividadePosVenda;

    @Column(name = "RECURSOS_CONHECIMENTO")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> recursosConhecimento;

    @Column(name = "RECURSOS_AVALIACAO")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> recursosAvaliacao;

    @Column(name = "RECURSOS_COMPRA")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> recursosCompra;

    @Column(name = "RECURSOS_ENTREGA")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> recursosEntrega;

    @Column(name = "RECURSOS_POS_VENDA")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> recursosPosVenda;

    @Column(name = "PARCEIROS_CHAVE")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> parceirosConhecimento;

    @Column(name = "PARCEIROS_AVALIACAO")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> parceirosAvaliacao;

    @Column(name = "PARCEIROS_COMPRA")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> parceirosCompra;

    @Column(name = "PARCEIROS_ENTREGA")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> parceirosEntrega;

    @Column(name = "PARCEIROS_POS_VENDA")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> parceirosPosVenda;

    @ManyToOne
    @JoinColumn(name = "AMBC_COD")
    private AmbienteCanvasModel ambienteCanvasModel;

}

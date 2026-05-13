package com.github.davidpotentini.cerne2.models.canvas;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Entity(name = "VALUE_PROPOSITION_CANVAS")
@Table
@Getter
@Setter
public class ValuePropostionCanvasModel {

    @Id
    private Long ambcCod;

    @Column(name = "CRIADORES_GANHO")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> criadoresGanho;

    @Column(name = "PRODUTOS_SERVICOS")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> produtosServicos;

    @Column(name = "ALIVIO_DORES")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> alivioDores;

    @Column(name = "GANHOS")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> ganhos;

    @Column(name = "DORES")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> dores;

    @Column(name = "TAREFAS_CLIENTES")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> tarefasClientes;

    @JoinColumn(name = "AMBC_COD")
    @MapsId("ambcCod")
    @OneToOne
    private AmbienteCanvasModel ambienteCanvasModel;

}

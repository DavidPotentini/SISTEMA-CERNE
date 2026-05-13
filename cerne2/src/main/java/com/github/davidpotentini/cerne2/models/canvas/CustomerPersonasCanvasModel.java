package com.github.davidpotentini.cerne2.models.canvas;

import com.github.davidpotentini.cerne2.models.canvas.ids.CustomerPersonasCanvasId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Entity(name = "CUSTOMER_PERSONAS_CANVAS")
@Table
@Getter
@Setter
public class CustomerPersonasCanvasModel {

    @Id
    @Column(name = "PERSONA_COD")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long personaCod;

//    @EmbeddedId
//    private CustomerPersonasCanvasId customerPersonasCanvasId;

    @Column(name = "ATRIBUTOS")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> atributos;

    @JoinColumn(name = "AMBC_COD")
    @ManyToOne
    private AmbienteCanvasModel ambienteCanvasModel;

}

package com.github.davidpotentini.cerne2.models.formularios;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.Map;

@Entity(name = "FORMULARIOS")
@Table
@Getter
@Setter
public class FormularioModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FRM_COD")
    private Long frmCod;

    @Column(name = "NOME")
    private String nome;

    @Column(name = "PROPOSITO")
    private String proposito;

    @Column(name = "JSON_CONFIG")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> jsonConfig;

    @OneToMany(mappedBy = "formularioModel")
    private List<FormularioRespostasModel> formularioRespostasModelList;
}

package com.github.davidpotentini.cerne2.models.formularios.ids;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
@Getter
@Setter
public class FormularioRespostasId implements Serializable {

    @Column(name = "FRM_COD")
    private Long frmCod;

    @Column(name = "PESSOA_COD")
    private Long pessoaCod;
}

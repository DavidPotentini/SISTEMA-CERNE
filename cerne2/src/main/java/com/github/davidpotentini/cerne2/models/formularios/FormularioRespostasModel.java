package com.github.davidpotentini.cerne2.models.formularios;

import com.github.davidpotentini.cerne2.models.formularios.ids.FormularioRespostasId;
import com.github.davidpotentini.cerne2.models.pessoas.PessoasModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Entity(name = "FORMULARIOS_RESPOSTAS")
@Table
@Getter
@Setter
public class FormularioRespostasModel {

    @EmbeddedId
    FormularioRespostasId formularioRespostasId;

    @Column(name = "RESPOSTA")
    @JdbcTypeCode(SqlTypes.JSON)
    Map<String, Object> resposta;

    @JoinColumn(name = "PESSOA_COD")
    @MapsId("pessoaCod")
    @ManyToOne
    PessoasModel pessoasModel;

    @JoinColumn(name = "FRM_COD")
    @MapsId("frmCod")
    @ManyToOne
    FormularioModel formularioModel;

}

package com.github.davidpotentini.cerne2.models.pessoas;

import com.github.davidpotentini.cerne2.enums.EPessoaExterna;
import com.github.davidpotentini.cerne2.enums.ETipoEmpreendimento;
import com.github.davidpotentini.cerne2.models.formularios.FormularioRespostasModel;
import com.github.davidpotentini.cerne2.models.informacoesgeraisincubadas.IncubadasModel;
import com.github.davidpotentini.cerne2.models.planejamentoestrategico.TarefasModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Entity (name = "PESSOAS")
@Table
@Getter
@Setter
public class PessoasModel {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "PESSOA_COD")
    private Long pessoaCod;

    @Column (name = "NOME")
    private String nome;

    @Column (name = "EMAIL")
    private String email;

    @Column (name = "CPF")
    private String cpf;

    @Column (name = "CARGO")
    private String cargo;

    @Column (name = "VLD_PESSOA_EXTERNA")
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private EPessoaExterna ePessoaExterna;

    @ManyToOne
    @JoinColumn(name = "INC_COD")
    private IncubadasModel incubadasModel;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "TIPO_EMPREENDIMENTO")
    private ETipoEmpreendimento tipoEmpreendimento;

    @OneToMany (mappedBy = "pessoasModel")
    private List<TarefasModel> tarefasModelList;

    @OneToMany (mappedBy = "pessoasModel")
    private List<FormularioRespostasModel> formularioRespostasModelList;
}

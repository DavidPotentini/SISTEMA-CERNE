package com.github.davidpotentini.cerne2.models.pessoas;

import com.github.davidpotentini.cerne2.enums.ETipoEmpreendimento;
import com.github.davidpotentini.cerne2.models.informacoesgeraisincubadas.IncubadasModel;
import com.github.davidpotentini.cerne2.models.planejamentoestrategico.TarefasModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    @ManyToOne
    @JoinColumn(name = "INC_COD")
    private IncubadasModel incubadasModel;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_EMPREENDIMENTO")
    private ETipoEmpreendimento tipoEmpreendimento;

    @OneToMany (mappedBy = "pessoasModel")
    private List<TarefasModel> tarefasModelList;
}

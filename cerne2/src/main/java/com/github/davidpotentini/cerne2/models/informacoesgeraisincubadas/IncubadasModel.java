package com.github.davidpotentini.cerne2.models.informacoesgeraisincubadas;

import com.github.davidpotentini.cerne2.enums.EStatusIncubacao;
import com.github.davidpotentini.cerne2.models.arquivo.ArquivosIncubadasModel;
import com.github.davidpotentini.cerne2.models.canvas.AmbienteCanvasModel;
import com.github.davidpotentini.cerne2.models.enderecos.EnderecosModel;
import com.github.davidpotentini.cerne2.models.pessoas.PessoasModel;
import com.github.davidpotentini.cerne2.models.planejamentoestrategico.PlanejamentoEstrategicoModel;
import com.github.davidpotentini.cerne2.models.validacaohipotese.QuadroValidacaoHipoteseModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.List;

@Entity(name = "INCUBADAS")
@Table
@Getter
@Setter
public class IncubadasModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "INC_COD")
    private Long incCod;

    @Column(name = "NOME")
    private String nome;

    @Column(name = "DATA_INICIO_INCUBACAO")
    private LocalDate dataInicioIncubacao;

    @Column(name = "EMAIL")
    private String email;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "STATUS_INCUBACAO")
    private EStatusIncubacao eStatusIncubacao;

    @Column(name = "DESCRICAO")
    private String descricao;
//
//    @Column(name = "DOCUMENTACAO")
//    private String documentacao;

    @JoinColumn(name = "END_COD")
    @OneToOne(cascade = CascadeType.PERSIST)
    private EnderecosModel enderecosModel;

    @OneToMany(mappedBy = "incubadasModel")
    private List<PessoasModel> pessoasModelList;

    @OneToMany(mappedBy = "incubadasModel")
    private List<AmbienteCanvasModel> ambienteCanvasModelList;

    @OneToMany(mappedBy = "incubadasModel")
    private List<QuadroValidacaoHipoteseModel> quadroValidacaoHipoteseModelList;

    @OneToMany(mappedBy = "incubadasModel")
    private List<ArquivosIncubadasModel> arquivosIncubadasModelList;

    @OneToMany(mappedBy = "incubadasModel")
    private List<PlanejamentoEstrategicoModel> planejamentoEstrategicoModelList;
}

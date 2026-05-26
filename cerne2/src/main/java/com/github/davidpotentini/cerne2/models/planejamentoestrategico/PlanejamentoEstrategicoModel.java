package com.github.davidpotentini.cerne2.models.planejamentoestrategico;

import com.github.davidpotentini.cerne2.enums.ETipoEmpreendimento;
import com.github.davidpotentini.cerne2.models.informacoesgeraisincubadas.IncubadasModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.List;

@Entity(name = "PLANEJAMENTO_ESTRATEGICO")
@Table
@Getter
@Setter
public class PlanejamentoEstrategicoModel {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column(name = "PES_COD")
    private Long pesCod;

    @Column(name = "NOME")
    private String nome;

    @Column(name = "DATA_INICIO")
    private LocalDate dataInicio;

    @Column(name = "DATA_TERMINO")
    private LocalDate dataTermino;

    @Enumerated
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "VLD_TIPO_EMPREENDIMENTO")
    private ETipoEmpreendimento vldTipoEmpreendimento;

    @JoinColumn(name = "INC_COD")
    @ManyToOne
    private IncubadasModel incubadasModel;

    @OneToMany(mappedBy = "planejamentoEstrategicoModel")
    private List<ProjetosModel> projetosModelList;
}

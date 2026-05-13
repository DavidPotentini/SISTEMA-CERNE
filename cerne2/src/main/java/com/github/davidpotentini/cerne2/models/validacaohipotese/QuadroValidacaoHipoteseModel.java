package com.github.davidpotentini.cerne2.models.validacaohipotese;

import com.github.davidpotentini.cerne2.models.informacoesgeraisincubadas.IncubadasModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity(name = "QUADRO_VALIDACAO_HIPOTESE")
@Table
@Getter
@Setter
public class QuadroValidacaoHipoteseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "QVH_COD")
    private Long qvhCod;

    @Column(name = "TITULO_QUADRO")
    private String tituloQuadro;

    @JoinColumn(name = "INC_COD")
    @ManyToOne
    private IncubadasModel incubadasModel;

    @OneToMany(mappedBy = "quadroValidacaoHipoteseModel")
    private List<HipoteseModel> hipoteseModelList;
}

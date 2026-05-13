package com.github.davidpotentini.cerne2.models.validacaohipotese;

import com.github.davidpotentini.cerne2.enums.EBlocoLeanCanvas;
import com.github.davidpotentini.cerne2.enums.EDecisaoHipotese;
import com.github.davidpotentini.cerne2.enums.EResultadoHipotese;
import com.github.davidpotentini.cerne2.enums.EStatusHipotese;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity(name = "HIPOTESE")
@Table
@Getter
@Setter
public class HipoteseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HIP_COD")
    private Long hipCod;

    @Column(name = "TITULO_QUADRO")
    private String tituloQuadro;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "BLOCO_LEAN_CANVAS")
    private EBlocoLeanCanvas vldBlocoLeanCanvas;

    @Column(name = "HIPOTESE")
    private String hipotese;

    @Column(name = "EXPERIMENTO")
    private String experimento;

    @Column(name = "METRICA")
    private String metrica;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "STATUS")
    private EStatusHipotese vldStatusHipotese;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "VLD_RESULTADO_HIPOTESE")
    private EResultadoHipotese vldResultadoHipotese;

    @Column(name = "RESULTADO_DETALHAMENTO")
    private String resultadoDetalhamento;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "VLD_DECISAO_HIPOTESE")
    private EDecisaoHipotese vldDecisaoHipotese;

    @Column(name = "DECISAO_DETALHAMENTO")
    private String decisaoDetalhamento;

    @ManyToOne
    @JoinColumn(name = "QVH_COD")
    private QuadroValidacaoHipoteseModel quadroValidacaoHipoteseModel;

}

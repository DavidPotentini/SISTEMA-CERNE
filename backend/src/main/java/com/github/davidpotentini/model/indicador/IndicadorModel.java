package com.github.davidpotentini.model.indicador;

import com.github.davidpotentini.enums.EAtivoInativo;
import com.github.davidpotentini.enums.EOrigemIndicador;
import com.github.davidpotentini.enums.EPeriodicidade;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "INDICADORES")
@Getter
@Setter
public class IndicadorModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IND_COD")
    private Long indCod;

    @Column(name = "NOME", nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "ORIGEM", nullable = false)
    private EOrigemIndicador origem = EOrigemIndicador.METODOLOGIA_CERNE;

    @Column(name = "PRTC_COD")
    private Long prtcCod;

    @Column(name = "CIC_COD")
    private Long cicCod;

    @Column(name = "RESP_PES_COD")
    private Long respPesCod;

    @Column(name = "UNIDADE")
    private String unidade;

    @Enumerated(EnumType.STRING)
    @Column(name = "PERIODICIDADE")
    private EPeriodicidade periodicidade = EPeriodicidade.TRIMESTRAL;

    @Enumerated(EnumType.STRING)
    @Column(name = "SITUACAO", nullable = false)
    private EAtivoInativo situacao = EAtivoInativo.ATIVO;
}

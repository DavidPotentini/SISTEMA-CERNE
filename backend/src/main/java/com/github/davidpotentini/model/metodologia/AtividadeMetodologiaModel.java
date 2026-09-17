package com.github.davidpotentini.model.metodologia;

import com.github.davidpotentini.enums.EAtivoInativo;
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
@Table(name = "ATIVIDADES_METODOLOGIA")
@Getter
@Setter
public class AtividadeMetodologiaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AME_COD")
    private Long ameCod;

    @Column(name = "PRT_COD", nullable = false)
    private Long prtCod;

    @Column(name = "AGR_COD")
    private Long agrCod;

    @Column(name = "ORDEM", nullable = false)
    private Integer ordem;

    @Column(name = "NOME", nullable = false)
    private String nome;

    @Column(name = "OBSERVACOES")
    private String observacoes;

    @Column(name = "POR_EMPREENDIMENTO", nullable = false)
    private boolean porEmpreendimento = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "SITUACAO", nullable = false)
    private EAtivoInativo situacao = EAtivoInativo.ATIVO;
}

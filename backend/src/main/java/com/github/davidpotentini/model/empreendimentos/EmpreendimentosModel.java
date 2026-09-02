package com.github.davidpotentini.model.empreendimentos;

import com.github.davidpotentini.enums.EEstagioIncubacao;
import com.github.davidpotentini.enums.ENivelMaturidade;
import com.github.davidpotentini.enums.ESituacaoContrato;
import com.github.davidpotentini.enums.EStatusEmpreendimento;
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

import java.time.LocalDate;

/**
 * Empreendimento (startup) incubado — schema do tenant. O vínculo com o ciclo é feito à parte, por
 * {@code CICLO_EMPREENDIMENTOS} (não há coluna própria aqui). O contato principal entre os membros da
 * startup é a pessoa {@code REPRESENTANTE_LEGAL} de {@code PESSOAS_EMPREENDIMENTO}; os documentos ficam
 * em {@code DOCUMENTOS_EMPREENDIMENTO}.
 */
@Entity
@Table(name = "EMPREENDIMENTOS")
@Getter
@Setter
public class EmpreendimentosModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EMP_COD")
    private Long empCod;

    @Column(name = "NOME", nullable = false)
    private String nome;

    @Column(name = "CNPJ")
    private String cnpj;

    @Column(name = "CNAE")
    private String cnae;

    @Column(name = "ATIVIDADE_ECONOMICA")
    private String atividadeEconomica;

    @Column(name = "INSTAGRAM")
    private String instagram;

    @Column(name = "SITE")
    private String site;

    @Column(name = "EMAIL")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "SITUACAO_CONTRATO")
    private ESituacaoContrato situacaoContrato;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTAGIO")
    private EEstagioIncubacao estagio = EEstagioIncubacao.IDEACAO;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private EStatusEmpreendimento status = EStatusEmpreendimento.ATIVO;

    @Enumerated(EnumType.STRING)
    @Column(name = "NIVEL_MATURIDADE")
    private ENivelMaturidade nivelMaturidade;

    @Column(name = "ENTRADA")
    private LocalDate entrada;

    @Column(name = "SAIDA")
    private LocalDate saida;
}

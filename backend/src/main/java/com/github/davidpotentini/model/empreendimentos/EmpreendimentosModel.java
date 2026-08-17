package com.github.davidpotentini.model.empreendimentos;

import com.github.davidpotentini.enums.EEstagioEmpreendimento;
import com.github.davidpotentini.enums.EModalidadeFisica;
import com.github.davidpotentini.enums.ESituacaoEmpreendimento;
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
 * Empreendimento (startup) incubado — schema do tenant. Dois papéis distintos: {@code respPesCod}
 * ({@code RESP_PES_COD} → {@code PESSOAS}) é o responsável interno — a pessoa da equipe da
 * incubadora encarregada do empreendimento (nome vem de {@code public.CONTAS} por join); o contato
 * principal entre os membros da startup é a pessoa {@code PRINCIPAL} de {@code PESSOAS_EMPREENDIMENTO}.
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

    @Column(name = "SETOR")
    private String setor;

    @Enumerated(EnumType.STRING)
    @Column(name = "MODALIDADE_FISICA")
    private EModalidadeFisica modalidadeFisica;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTAGIO")
    private EEstagioEmpreendimento estagio = EEstagioEmpreendimento.IDEACAO;

    @Enumerated(EnumType.STRING)
    @Column(name = "SITUACAO", nullable = false)
    private ESituacaoEmpreendimento situacao = ESituacaoEmpreendimento.EM_ANALISE;

    @Column(name = "ENTRADA")
    private LocalDate entrada;

    /** Responsável interno: pessoa da equipe da incubadora ({@code PESSOAS}, ref. fraca via {@code PES_COD}). */
    @Column(name = "RESP_PES_COD")
    private Long respPesCod;
}

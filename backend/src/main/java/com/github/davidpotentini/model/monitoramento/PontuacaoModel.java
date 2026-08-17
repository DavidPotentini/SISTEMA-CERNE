package com.github.davidpotentini.model.monitoramento;

import com.github.davidpotentini.enums.EEixoCerne;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Nota (0–5) de um eixo CERNE numa avaliação ({@code AVALIACAO_PONTUACOES}). Chave composta por
 * {@code AVA_COD} + {@code DIMENSAO} — uma nota por eixo por avaliação.
 */
@Entity
@Table(name = "AVALIACAO_PONTUACOES")
@IdClass(PontuacaoId.class)
@Getter
@Setter
public class PontuacaoModel {

    @Id
    @Column(name = "AVA_COD")
    private Long avaCod;

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "DIMENSAO")
    private EEixoCerne dimensao;

    @Column(name = "PONTUACAO")
    private Short pontuacao;
}

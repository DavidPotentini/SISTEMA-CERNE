package com.github.davidpotentini.model.metodologia;

import com.github.davidpotentini.enums.ESituacaoVersao;
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

import java.time.LocalDateTime;

/**
 * Versão da metodologia CERNE — raiz da árvore {@code VERSAO → PROCESSO → PRATICA}. Só a versão
 * {@code VIGENTE} é editável; publicar clona a árvore numa nova versão e marca a anterior
 * {@code HISTORICA} (fluxo da aba "Indicadores e Publicação", futura). Schema do tenant.
 */
@Entity
@Table(name = "VERSOES_METODOLOGIA")
@Getter
@Setter
public class VersaoMetodologiaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VER_COD")
    private Long verCod;

    @Column(name = "VERSAO", nullable = false)
    private String versao;

    /** Preenchido só na publicação (aba futura); {@code null} enquanto rascunho/vigente não publicada. */
    @Column(name = "PUBLICADA_EM")
    private LocalDateTime publicadaEm;

    /** Quem publicou → {@code PESSOAS(PES_COD)}. Guardado como código; sem FK mapeada aqui. */
    @Column(name = "PUB_PES_COD")
    private Long pubPesCod;

    @Enumerated(EnumType.STRING)
    @Column(name = "SITUACAO", nullable = false)
    private ESituacaoVersao situacao = ESituacaoVersao.RASCUNHO;

    /** Só faz sentido no RASCUNHO: há mudanças ainda não publicadas? */
    @Column(name = "ALTERADA", nullable = false)
    private boolean alterada = false;

    @Column(name = "RESUMO")
    private String resumo;
}

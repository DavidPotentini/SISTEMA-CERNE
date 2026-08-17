package com.github.davidpotentini.repository.metodologia;

import com.github.davidpotentini.enums.ESituacaoVersao;
import com.github.davidpotentini.model.metodologia.VersaoMetodologiaModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface VersaoMetodologiaRepository extends JpaRepository<VersaoMetodologiaModel, Long> {

    /** A versão nesta situação (RASCUNHO e VIGENTE são únicas). */
    Optional<VersaoMetodologiaModel> findFirstBySituacaoOrderByVerCodDesc(ESituacaoVersao situacao);

    /** Histórico de publicações (VIGENTE + HISTORICA), mais recentes primeiro. */
    List<VersaoMetodologiaModel> findBySituacaoInOrderByVerCodDesc(Collection<ESituacaoVersao> situacoes);

    /** Quantas versões já foram publicadas (não-rascunho), para numerar a próxima (v1, v2, …). */
    long countBySituacaoNot(ESituacaoVersao situacao);
}

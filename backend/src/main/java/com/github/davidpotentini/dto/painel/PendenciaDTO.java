package com.github.davidpotentini.dto.painel;

import com.github.davidpotentini.enums.ETipoPendencia;

import java.time.LocalDate;

/**
 * Uma pendência (tela Pendências) — linha da lista unificada (só saída). O {@code tipo} diz a
 * seção; {@code referenciaId} é o código da origem usado na navegação (atividade → {@code atpCod},
 * evidência → {@code evdCod}, meta → {@code indCod}). {@code titulo} é o nome principal;
 * {@code processoNome}/{@code praticaNome} situam o item na estrutura CERNE; {@code detalhe} é um
 * complemento (empreendimento na atividade, motivo na evidência) e {@code prazo} a data limite quando
 * houver (atividade/meta; nulo para evidência). {@code respPesCod} é o responsável da origem
 * (atividade → responsável; evidência → quem registrou; meta → responsável do indicador) e alimenta o
 * filtro padrão da tela.
 */
public record PendenciaDTO(
        ETipoPendencia tipo,
        Long referenciaId,
        String titulo,
        String processoNome,
        String praticaNome,
        String detalhe,
        LocalDate prazo,
        Long respPesCod
) {
}

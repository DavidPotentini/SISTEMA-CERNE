package com.github.davidpotentini.dto.evidencia;

import com.github.davidpotentini.enums.EStatusEvidencia;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Uma versão da evidência (entrada + saída).
 *
 * <p><b>Entrada</b> (registrar/corrigir): {@code titulo}, {@code atpCod}, {@code arqCod}. <b>Saída</b>:
 * acrescenta os rótulos derivados ({@code atividadeNome} e o processo/prática a que a atividade
 * pertence — {@code processoNome}, {@code praticaNome} —, {@code arquivoNome}, {@code responsavel}) e
 * o {@code motivoCorrecao} — o motivo da rejeição, presente só quando o {@code status} é
 * {@code CORRECAO_SOLICITADA}. O {@code status} nasce {@code EM_VALIDACAO} no service — não é definido
 * pelo cliente.
 */
public record EvidenciaDTO(
        Long evdCod,
        Integer evdCodSeq,
        @NotBlank String titulo,
        @NotNull Long atpCod,
        String atividadeNome,
        String processoNome,
        String praticaNome,
        Long arqCod,
        String arquivoNome,
        EStatusEvidencia status,
        Long regPesCod,
        String responsavel,
        LocalDateTime data,
        String motivoCorrecao) {
}

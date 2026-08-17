package com.github.davidpotentini.dto.execucao;

import com.github.davidpotentini.enums.EStatusAtividade;
import jakarta.validation.constraints.NotNull;

/**
 * Mudança de status de uma atividade no acompanhamento de execução. O alvo só pode ser um estado
 * manual ({@code PLANEJADA}, {@code EM_ANDAMENTO} ou {@code CONCLUIDA}); concluir exige que a
 * atividade tenha ao menos uma evidência e que todas as versões correntes estejam validadas.
 */
public record MudarStatusAtividadeDTO(
        @NotNull EStatusAtividade status) {
}

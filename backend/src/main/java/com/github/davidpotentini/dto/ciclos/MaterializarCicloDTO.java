package com.github.davidpotentini.dto.ciclos;

import java.util.List;

/**
 * Corpo do "Gerar do ciclo": as incubadas participantes escolhidas. As atividades marcadas "da
 * incubada" são duplicadas por empreendimento desta lista. Vazio/nulo = nenhuma incubada (as atividades
 * da incubada não são geradas).
 */
public record MaterializarCicloDTO(
        List<Long> empCods
) {
}

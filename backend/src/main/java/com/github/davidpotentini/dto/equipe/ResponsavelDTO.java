package com.github.davidpotentini.dto.equipe;

/** Candidato a responsável: pessoa da equipe da incubadora ({@code PES_COD} + nome). */
public record ResponsavelDTO(Long pesCod, String nome) {
}

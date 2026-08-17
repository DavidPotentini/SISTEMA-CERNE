package com.github.davidpotentini.dto.empreendimentos;

/** Candidato a responsável interno: pessoa da equipe da incubadora ({@code PES_COD} + nome). */
public record ResponsavelDTO(Long pesCod, String nome) {
}

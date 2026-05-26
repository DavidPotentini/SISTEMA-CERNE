package com.github.davidpotentini.cerne2.dto.login;

import com.github.davidpotentini.cerne2.enums.ERole;
import com.github.davidpotentini.cerne2.enums.ETipoEmpreendimento;

/**
 * Contexto do usuário autenticado. O front-end deve guardar {@code nomeSchema}
 * e enviá-lo no header {@code X-Tenant} nas requisições seguintes. O par
 * {@code tipoEmpreendimento} + {@code incCod} é o que resolve, futuramente,
 * quais telas o usuário visualiza.
 */
public record LoginRespostaDTO(
        Long usnCod,
        String email,
        Long pessoaCod,
        String nome,
        Long tntCod,
        String nomeEmpresa,
        String nomeSchema,
        ETipoEmpreendimento tipoEmpreendimento,
        ERole role,
        Long incCod,
        String token) {
}

package com.github.davidpotentini.dto.usuarios;

import com.github.davidpotentini.enums.EStatusConta;

/**
 * Linha da listagem "Usuários da plataforma". {@code incubadora}/{@code papel} vêm do
 * vínculo com o tenant: {@code incCod}/{@code incubadora} da própria conta, e
 * {@code papCod}/{@code papel} lidos no schema do tenant (papel local). Nulos ("—") quando
 * a conta não tem incubadora/papel; para o admin da plataforma, papel é um rótulo fixo.
 */
public record UsuarioResumoDTO(
        Long ctaCod,
        String nome,
        String email,
        Long incCod,
        String incubadora,
        Long papCod,
        String papel,
        EStatusConta status
) {
}

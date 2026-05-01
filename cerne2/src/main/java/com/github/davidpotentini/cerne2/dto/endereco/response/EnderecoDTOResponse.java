package com.github.davidpotentini.cerne2.dto.endereco.response;

public record EnderecoDTOResponse(
        Long endCod,
        String cidade,
        String rua,
        String bairro,
        String numero,
        String complemento,
        String estado,
        String uf
) {
}

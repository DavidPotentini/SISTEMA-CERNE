package com.github.davidpotentini.cerne2.dto.endereco.request;

public record EnderecoDTORequest(
        String cidade,
        String rua,
        String bairro,
        String numero,
        String complemento,
        String estado,
        String uf
) {
}

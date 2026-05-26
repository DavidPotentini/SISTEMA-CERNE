package com.github.davidpotentini.cerne2.dto.cadastro;

public record CadastroUsuarioDTO(
        String nome,
        String email,
        String cpf,
        String senha,
        String nomeEmpresa,
        Long tntCod) {
}

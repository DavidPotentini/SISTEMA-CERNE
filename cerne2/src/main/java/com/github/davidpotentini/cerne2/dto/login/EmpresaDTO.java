package com.github.davidpotentini.cerne2.dto.login;

/**
 * Item da lista de empresas (tenants) exibida no seletor da tela de login.
 */
public record EmpresaDTO(
        Long tntCod,
        String nomeEmpresa) {
}

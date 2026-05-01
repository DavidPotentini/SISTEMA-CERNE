package com.github.davidpotentini.cerne2.dto.cadastro;

import com.github.davidpotentini.cerne2.models.cadastro.TenantsModel;
import com.github.davidpotentini.cerne2.models.cadastro.UsersModel;

public record CadastroUsuarioDTO(
        String email,
        String senha,
        String nomeEmpresa,
        Long tntCod) {

    public TenantsModel mapToTenants(){
        TenantsModel tenantsModel = new TenantsModel();

        tenantsModel.setNomeEmpresa(this.nomeEmpresa);

        tenantsModel.setNomeSchema(this.nomeEmpresa + "_schema");

        return tenantsModel;
    }

    public UsersModel mapToUser(){
        UsersModel usersModel = new UsersModel();

        usersModel.setEmail(this.email);
        usersModel.setSenha(this.senha);

        return usersModel;
    }
}

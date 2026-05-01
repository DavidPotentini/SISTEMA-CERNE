package com.github.davidpotentini.cerne2.models.cadastro;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "TENANTS")
@Getter
@Setter
public class TenantsModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TNT_COD")
    private Long tntCod;

    @Column(name = "NOME_EMPRESA")
    private String nomeEmpresa;

    @Column(name = "NOME_SCHEMA")
    private String nomeSchema;

    @OneToMany(mappedBy = "tenantsModel")
    private List<UsersModel> usersModel;
}

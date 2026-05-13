package com.github.davidpotentini.cerne2.models.cadastro;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "USERS", schema = "public")
@Getter
@Setter
public class UsersModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USN_COD")
    private Long usnCod;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "SENHA")
    private String senha;

    @ManyToOne
    @JoinColumn(name = "TNT_COD")
    private TenantsModel tenantsModel;
}

package com.github.davidpotentini.cerne2.models.cadastro;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "USER_PERMISSIONS")
@Getter
@Setter
public class UserPermissionsModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long usnpCod;

    @Column
    private Long usnCod;

    @ManyToOne
    @JoinColumn(name = "ROLE_COD")
    private RolesModel rolesModel;

}

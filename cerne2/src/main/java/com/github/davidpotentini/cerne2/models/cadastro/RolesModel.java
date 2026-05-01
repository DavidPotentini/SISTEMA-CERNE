package com.github.davidpotentini.cerne2.models.cadastro;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Table
@Entity(name = "ROLES")
@Getter
@Setter
public class RolesModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long roleCod;

    @Column
    private String roleDescr;

    @OneToMany(mappedBy = "rolesModel")
    private List<UserPermissionsModel> userPermissionsModelList;
}

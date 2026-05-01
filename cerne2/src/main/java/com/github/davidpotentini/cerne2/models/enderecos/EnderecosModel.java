package com.github.davidpotentini.cerne2.models.enderecos;

import com.github.davidpotentini.cerne2.models.informacoesgeraisincubadas.IncubadasModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity (name = "ENDERECOS")
@Table
@Getter
@Setter
public class EnderecosModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "END_COD")
    private Long endCod;

    @Column(name = "CIDADE")
    private String cidade;

    @Column(name = "RUA")
    private String rua;

    @Column(name = "BAIRRO")
    private String bairro;

    @Column(name = "NUMERO")
    private String numero;

    @Column(name = "COMPLEMENTO")
    private String complemento;

    @Column(name = "ESTADO")
    private String estado;

    @Column(name = "UF")
    private String uf;

}

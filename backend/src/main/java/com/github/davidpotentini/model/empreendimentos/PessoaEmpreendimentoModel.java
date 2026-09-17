package com.github.davidpotentini.model.empreendimentos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "PESSOAS_EMPREENDIMENTO")
@Getter
@Setter
public class PessoaEmpreendimentoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PSE_COD")
    private Long pseCod;

    @Column(name = "EMP_COD", nullable = false)
    private Long empCod;

    @Column(name = "NOME", nullable = false)
    private String nome;

    @Column(name = "REPRESENTANTE_LEGAL", nullable = false)
    private boolean representanteLegal = false;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "TELEFONE")
    private String telefone;
}

package com.github.davidpotentini.model.contas;

import com.github.davidpotentini.enums.EStatusConta;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/** Identidade de login única (schema {@code public}); a senha é hash BCrypt. */
@Entity
@Table(name = "CONTAS")
@Getter
@Setter
public class ContasModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CTA_COD")
    private Long ctaCod;

    @Column(name = "NOME", nullable = false)
    private String nome;

    @Column(name = "EMAIL", nullable = false, unique = true)
    private String email;

    @Column(name = "SENHA", nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private EStatusConta status = EStatusConta.CONVIDADO;

    @Column(name = "ADMIN_PLATAFORMA", nullable = false)
    private boolean adminPlataforma;

    /** Nulo para o administrador da plataforma. */
    @Column(name = "INC_COD")
    private Long incCod;
}

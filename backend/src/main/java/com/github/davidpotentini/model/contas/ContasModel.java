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

/**
 * Identidade de login única (schema {@code public}). Vale para todos: administrador da
 * plataforma e pessoas de incubadora. A senha é hash BCrypt. O vínculo com a incubadora
 * é 1:1 via {@code incCod} (nulo quando {@code adminPlataforma}).
 */
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

    /** Situação da conta (VLD_STATUS_CONTA). Nasce CONVIDADO no convite. */
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private EStatusConta status = EStatusConta.CONVIDADO;

    @Column(name = "ADMIN_PLATAFORMA", nullable = false)
    private boolean adminPlataforma;

    /** Incubadora da conta (1:1). Nulo para o administrador da plataforma. */
    @Column(name = "INC_COD")
    private Long incCod;
}

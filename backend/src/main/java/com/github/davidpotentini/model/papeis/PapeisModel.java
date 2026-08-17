package com.github.davidpotentini.model.papeis;

import com.github.davidpotentini.enums.EAtivoInativo;
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

/** Papel local da incubadora (schema do tenant). Só o nome é necessário no login. */
@Entity
@Table(name = "PAPEIS")
@Getter
@Setter
public class PapeisModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PAP_COD")
    private Long papCod;

    @Column(name = "NOME", nullable = false)
    private String nome;

    /** Situação do papel (ATIVO/INATIVO). Papéis inativos não são ofertados na atribuição. */
    @Enumerated(EnumType.STRING)
    @Column(name = "SITUACAO", nullable = false)
    private EAtivoInativo situacao = EAtivoInativo.ATIVO;
}

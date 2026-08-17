package com.github.davidpotentini.model.pessoas;

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

/**
 * Vínculo local de uma conta global ({@code public.CONTAS}) com este tenant. Nome/e-mail
 * <b>não</b> são duplicados aqui — vêm por JOIN em {@code VW_PESSOAS}. {@code ctaCod} é
 * referência fraca cross-schema (sem FK); {@code papCod} é o papel local (FK em PAPEIS).
 */
@Entity
@Table(name = "PESSOAS")
@Getter
@Setter
public class PessoasModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PES_COD")
    private Long pesCod;

    @Column(name = "CTA_COD", nullable = false, unique = true)
    private Long ctaCod;

    @Column(name = "PAP_COD")
    private Long papCod;
}

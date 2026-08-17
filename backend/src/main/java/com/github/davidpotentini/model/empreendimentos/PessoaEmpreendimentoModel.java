package com.github.davidpotentini.model.empreendimentos;

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
 * Pessoa de um empreendimento (membro da startup; NÃO é usuário do sistema) — schema do tenant.
 * {@code principal} marca o responsável (contato principal perante a incubadora); no máximo um
 * por empreendimento.
 */
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

    @Column(name = "PAPEL")
    private String papel;

    @Column(name = "PRINCIPAL", nullable = false)
    private boolean principal = false;

    @Column(name = "CONTATO")
    private String contato;

    @Enumerated(EnumType.STRING)
    @Column(name = "SITUACAO", nullable = false)
    private EAtivoInativo situacao = EAtivoInativo.ATIVO;
}

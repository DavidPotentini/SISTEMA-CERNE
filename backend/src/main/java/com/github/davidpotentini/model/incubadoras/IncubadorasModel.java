package com.github.davidpotentini.model.incubadoras;

import com.github.davidpotentini.enums.ENivelIncubadora;
import com.github.davidpotentini.enums.EStatusIncubadora;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/** Incubadora (schema {@code public}) e também o registro do tenant: {@code nomeSchema} é a gaveta Postgres dele. */
@Entity
@Table(name = "INCUBADORAS")
@Getter
@Setter
public class IncubadorasModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "INC_COD")
    private Long incCod;

    @Column(name = "NOME", nullable = false)
    private String nome;

    @Column(name = "CNPJ")
    private String cnpj;

    @Column(name = "MANTENEDORA")
    private String mantenedora;

    @Column(name = "RESP_CTA_COD")
    private Long respCtaCod;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "TELEFONE")
    private String telefone;

    @Column(name = "CIDADE")
    private String cidade;

    @Convert(converter = NivelIncubadoraConverter.class)
    @Column(name = "NIVEL")
    private ENivelIncubadora nivel;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private EStatusIncubadora status;

    @Column(name = "NOME_SCHEMA", nullable = false, unique = true)
    private String nomeSchema;

    @Column(name = "CRIADA_EM")
    private LocalDateTime criadaEm;

    @Column(name = "ATIVADA_EM")
    private LocalDateTime ativadaEm;
}

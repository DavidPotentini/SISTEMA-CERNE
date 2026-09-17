package com.github.davidpotentini.model.ciclos;

import com.github.davidpotentini.enums.EStatusCiclo;
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

import java.time.LocalDate;

@Entity
@Table(name = "CICLOS")
@Getter
@Setter
public class CiclosModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CIC_COD")
    private Long cicCod;

    @Column(name = "NOME", nullable = false)
    private String nome;

    @Column(name = "INICIO")
    private LocalDate inicio;

    @Column(name = "FIM")
    private LocalDate fim;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private EStatusCiclo status = EStatusCiclo.ATIVO;

    @Column(name = "EM_FOCO", nullable = false)
    private boolean emFoco = false;
}

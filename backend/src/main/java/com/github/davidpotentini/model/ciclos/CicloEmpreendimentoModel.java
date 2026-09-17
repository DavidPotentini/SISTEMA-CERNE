package com.github.davidpotentini.model.ciclos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "CICLO_EMPREENDIMENTOS")
@IdClass(CicloEmpreendimentoId.class)
@Getter
@Setter
public class CicloEmpreendimentoModel {

    @Id
    @Column(name = "CIC_COD")
    private Long cicCod;

    @Id
    @Column(name = "EMP_COD")
    private Long empCod;
}

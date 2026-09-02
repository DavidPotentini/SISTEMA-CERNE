package com.github.davidpotentini.model.ciclos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Empreendimento incubado participante de um ciclo (tabela de junção {@code CICLO_EMPREENDIMENTOS}).
 * Definida ao "Gerar do ciclo": as atividades marcadas "da incubada" são duplicadas por participante.
 * Schema do tenant.
 */
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

package com.github.davidpotentini.model.monitoramento;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Participação de um empreendimento numa rodada de monitoramento (tabela de junção
 * {@code RODADA_INCUBADAS}). É a fonte de quem está na rodada; a avaliação de cada um é criada
 * depois, ao revisar ({@link AvaliacaoModel}).
 */
@Entity
@Table(name = "RODADA_INCUBADAS")
@IdClass(RodadaIncubadaId.class)
@Getter
@Setter
public class RodadaIncubadaModel {

    @Id
    @Column(name = "ROD_COD")
    private Long rodCod;

    @Id
    @Column(name = "EMP_COD")
    private Long empCod;
}

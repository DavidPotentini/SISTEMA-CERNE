package com.github.davidpotentini.repository.planejamento;

import com.github.davidpotentini.enums.EStatusPlanejamento;
import com.github.davidpotentini.model.planejamento.PlanejamentoModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlanejamentoRepository extends JpaRepository<PlanejamentoModel, Long> {

    /**
     * Planejamento vigente de um ciclo (o {@code PUBLICADO}). Pode haver vários registros para o
     * mesmo ciclo — os anteriores ficam {@code ENCERRADO} como histórico quando se regera —, mas no
     * máx. um {@code PUBLICADO} por vez.
     */
    Optional<PlanejamentoModel> findByCicCodAndStatus(Long cicCod, EStatusPlanejamento status);
}

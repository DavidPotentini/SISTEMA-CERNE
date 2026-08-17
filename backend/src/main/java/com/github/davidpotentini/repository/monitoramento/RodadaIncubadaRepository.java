package com.github.davidpotentini.repository.monitoramento;

import com.github.davidpotentini.model.monitoramento.RodadaIncubadaId;
import com.github.davidpotentini.model.monitoramento.RodadaIncubadaModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RodadaIncubadaRepository extends JpaRepository<RodadaIncubadaModel, RodadaIncubadaId> {

    List<RodadaIncubadaModel> findByRodCod(Long rodCod);
}

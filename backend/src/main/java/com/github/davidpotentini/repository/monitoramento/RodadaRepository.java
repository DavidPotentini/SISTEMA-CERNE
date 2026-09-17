package com.github.davidpotentini.repository.monitoramento;

import com.github.davidpotentini.model.monitoramento.RodadaModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RodadaRepository extends JpaRepository<RodadaModel, Long> {

    List<RodadaModel> findAllByOrderByRodCodDesc();
}

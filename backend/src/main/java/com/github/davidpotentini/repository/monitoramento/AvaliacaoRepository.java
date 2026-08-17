package com.github.davidpotentini.repository.monitoramento;

import com.github.davidpotentini.model.monitoramento.AvaliacaoModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AvaliacaoRepository extends JpaRepository<AvaliacaoModel, Long> {

    List<AvaliacaoModel> findByRodCod(Long rodCod);

    Optional<AvaliacaoModel> findByRodCodAndEmpCod(Long rodCod, Long empCod);
}

package com.github.davidpotentini.cerne2.repository.planejamentoestrategico;

import com.github.davidpotentini.cerne2.models.planejamentoestrategico.EvidenciasModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EvidenciasRepository extends JpaRepository<EvidenciasModel, Long> {
    Optional<List<EvidenciasModel>> findByTarefasModel_TrfCod(Long trfCod);

    void deleteByTarefasModel_TrfCod(Long trfCod);
}

package com.github.davidpotentini.cerne2.repository.planejamentoestrategico;

import com.github.davidpotentini.cerne2.models.planejamentoestrategico.ProjetosModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface ProjetosRepository extends JpaRepository<ProjetosModel, Long> {

    Optional<List<ProjetosModel>> findByPlanejamentoEstrategicoModel_PesCod(Long pesCod);
}

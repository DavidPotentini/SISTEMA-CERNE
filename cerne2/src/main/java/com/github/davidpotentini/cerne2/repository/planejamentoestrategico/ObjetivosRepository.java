package com.github.davidpotentini.cerne2.repository.planejamentoestrategico;

import com.github.davidpotentini.cerne2.models.planejamentoestrategico.ObjetivosModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ObjetivosRepository extends JpaRepository<ObjetivosModel, Long> {

    Optional<List<ObjetivosModel>> findByProjetosModel_PrjCod (Long prjCod);
}

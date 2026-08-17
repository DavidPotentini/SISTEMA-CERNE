package com.github.davidpotentini.repository.ciclos;

import com.github.davidpotentini.enums.EStatusCiclo;
import com.github.davidpotentini.model.ciclos.CiclosModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CiclosRepository extends JpaRepository<CiclosModel, Long> {

    /** Mais recentes primeiro. */
    List<CiclosModel> findAllByOrderByCicCodDesc();

    List<CiclosModel> findByStatus(EStatusCiclo status);

    Optional<CiclosModel> findByEmFocoTrue();
}

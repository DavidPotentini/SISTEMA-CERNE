package com.github.davidpotentini.cerne2.repository.planejamentoestrategico;

import com.github.davidpotentini.cerne2.models.planejamentoestrategico.TarefasModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TarefasRepository extends JpaRepository<TarefasModel, Long> {

    Optional<List<TarefasModel>> findByObjetivosModel_ObjCod(Long objCod);
}

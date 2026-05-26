package com.github.davidpotentini.cerne2.repository.arquivo;

import com.github.davidpotentini.cerne2.models.arquivo.ArquivosEvidenciasModel;
import com.github.davidpotentini.cerne2.models.arquivo.ids.ArquivosEvidenciasId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArquivosEvidenciasRepository extends JpaRepository<ArquivosEvidenciasModel, ArquivosEvidenciasId> {
    List<ArquivosEvidenciasModel> findByEvidenciasModel_EvdCod(Long evdCod);
}

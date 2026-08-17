package com.github.davidpotentini.repository.modelos;

import com.github.davidpotentini.model.modelos.AtividadeModeloModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AtividadeModeloRepository extends JpaRepository<AtividadeModeloModel, Long> {

    /** Atividades do modelo (agrupadas por prática no service). */
    List<AtividadeModeloModel> findByModCodOrderByAtmCodAsc(Long modCod);
}

package com.github.davidpotentini.repository.modelos;

import com.github.davidpotentini.model.modelos.ModeloModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModeloRepository extends JpaRepository<ModeloModel, Long> {

    /** Modelos da incubadora, mais recentes primeiro (listagem da tela). */
    List<ModeloModel> findAllByOrderByModCodDesc();
}

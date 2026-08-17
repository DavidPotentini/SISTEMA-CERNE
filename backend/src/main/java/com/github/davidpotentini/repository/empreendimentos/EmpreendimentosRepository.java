package com.github.davidpotentini.repository.empreendimentos;

import com.github.davidpotentini.model.empreendimentos.EmpreendimentosModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmpreendimentosRepository extends JpaRepository<EmpreendimentosModel, Long> {

    List<EmpreendimentosModel> findAllByOrderByNomeAsc();
}

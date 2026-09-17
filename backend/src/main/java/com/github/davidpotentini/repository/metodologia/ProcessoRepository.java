package com.github.davidpotentini.repository.metodologia;

import com.github.davidpotentini.model.metodologia.ProcessoModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProcessoRepository extends JpaRepository<ProcessoModel, Long> {

    List<ProcessoModel> findAllByOrderByOrdemAscPrcCodAsc();

    Optional<ProcessoModel> findFirstByOrderByOrdemDesc();
}

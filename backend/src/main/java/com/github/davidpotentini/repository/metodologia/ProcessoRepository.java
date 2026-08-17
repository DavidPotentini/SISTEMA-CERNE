package com.github.davidpotentini.repository.metodologia;

import com.github.davidpotentini.model.metodologia.ProcessoModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProcessoRepository extends JpaRepository<ProcessoModel, Long> {

    /** Processos da versão, na ordem dos accordions. */
    List<ProcessoModel> findByVerCodOrderByOrdemAscPrcCodAsc(Long verCod);

    /** Já existe um processo com essa ordem na versão? (ORDEM é UNIQUE no banco.) */
    boolean existsByVerCodAndOrdem(Long verCod, Integer ordem);

    /** Idem, mas ignorando um processo (para validar a ordem ao editar sem colidir consigo mesmo). */
    boolean existsByVerCodAndOrdemAndPrcCodNot(Long verCod, Integer ordem, Long prcCod);
}

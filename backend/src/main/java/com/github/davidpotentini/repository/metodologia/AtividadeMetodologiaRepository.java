package com.github.davidpotentini.repository.metodologia;

import com.github.davidpotentini.model.metodologia.AtividadeMetodologiaModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AtividadeMetodologiaRepository extends JpaRepository<AtividadeMetodologiaModel, Long> {

    List<AtividadeMetodologiaModel> findByPrtCodInOrderByOrdemAscNomeAsc(Collection<Long> prtCods);

    Optional<AtividadeMetodologiaModel> findFirstByPrtCodOrderByOrdemDesc(Long prtCod);

    List<AtividadeMetodologiaModel> findByPrtCod(Long prtCod);

    boolean existsByAgrCod(Long agrCod);
}

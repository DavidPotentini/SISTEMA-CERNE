package com.github.davidpotentini.repository.metodologia;

import com.github.davidpotentini.model.metodologia.IndicadorMetodologiaModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface IndicadorMetodologiaRepository extends JpaRepository<IndicadorMetodologiaModel, Long> {

    List<IndicadorMetodologiaModel> findByPrtCodInOrderByNomeAsc(Collection<Long> prtCods);

    List<IndicadorMetodologiaModel> findByPrtCod(Long prtCod);
}

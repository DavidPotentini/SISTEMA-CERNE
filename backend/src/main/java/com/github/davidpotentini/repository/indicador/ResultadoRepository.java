package com.github.davidpotentini.repository.indicador;

import com.github.davidpotentini.model.indicador.ResultadoModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ResultadoRepository extends JpaRepository<ResultadoModel, Long> {

    List<ResultadoModel> findByMetCodIn(Collection<Long> metCods);
}

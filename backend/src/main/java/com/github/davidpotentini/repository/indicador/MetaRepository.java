package com.github.davidpotentini.repository.indicador;

import com.github.davidpotentini.model.indicador.MetaModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface MetaRepository extends JpaRepository<MetaModel, Long> {

    /** Períodos (metas) de um indicador, em ordem do início de apuração. */
    List<MetaModel> findByIndCodOrderByDataInicioApuracaoAscMetCodAsc(Long indCod);

    /** Períodos dos indicadores informados — para contar totais na apuração (em lote). */
    List<MetaModel> findByIndCodIn(Collection<Long> indCods);
}

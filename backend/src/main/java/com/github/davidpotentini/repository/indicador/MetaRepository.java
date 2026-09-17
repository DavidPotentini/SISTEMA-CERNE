package com.github.davidpotentini.repository.indicador;

import com.github.davidpotentini.model.indicador.MetaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface MetaRepository extends JpaRepository<MetaModel, Long> {

    List<MetaModel> findByIndCodOrderByDataInicioApuracaoAscMetCodAsc(Long indCod);

    List<MetaModel> findByIndCodIn(Collection<Long> indCods);

    @Query(value = "SELECT COUNT(*) > 0 FROM INDICADOR_METAS WHERE IND_COD IN "
            + "(SELECT IND_COD FROM INDICADORES WHERE CIC_COD = :cicCod)", nativeQuery = true)
    boolean existsByCiclo(@Param("cicCod") Long cicCod);
}

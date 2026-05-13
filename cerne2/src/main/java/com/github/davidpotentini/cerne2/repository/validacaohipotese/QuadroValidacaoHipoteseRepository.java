package com.github.davidpotentini.cerne2.repository.validacaohipotese;

import com.github.davidpotentini.cerne2.models.validacaohipotese.QuadroValidacaoHipoteseModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuadroValidacaoHipoteseRepository extends JpaRepository<QuadroValidacaoHipoteseModel, Long> {

    @Query(value = "SELECT * FROM QUADRO_VALIDACAO_HIPOTESE WHERE INC_COD = :INC_COD",nativeQuery = true)
    List<QuadroValidacaoHipoteseModel> findQuadroValidacaoHipoteseByIncCod(@Param("INC_COD") Long incCod);
}

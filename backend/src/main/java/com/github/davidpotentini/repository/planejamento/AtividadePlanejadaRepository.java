package com.github.davidpotentini.repository.planejamento;

import com.github.davidpotentini.enums.EOrigemAtividade;
import com.github.davidpotentini.enums.EStatusAtividade;
import com.github.davidpotentini.model.planejamento.AtividadePlanejadaModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AtividadePlanejadaRepository extends JpaRepository<AtividadePlanejadaModel, Long> {

    List<AtividadePlanejadaModel> findByPlnCodOrderByOrdemAscAtpCodAsc(Long plnCod);

    Optional<AtividadePlanejadaModel> findFirstByPlnCodAndPrtcCodOrderByOrdemDesc(Long plnCod, Long prtcCod);

    List<AtividadePlanejadaModel> findByPlnCodAndPrtcCod(Long plnCod, Long prtcCod);

    long countByPlnCod(Long plnCod);

    long countByPlnCodAndStatus(Long plnCod, EStatusAtividade status);


    boolean existsByPlnCodAndOrigem(Long plnCod, EOrigemAtividade origem);

    boolean existsByPlnCodAndRespPesCodNotNull(Long plnCod);

    boolean existsByPlnCodAndPrazoNotNull(Long plnCod);

    boolean existsByPlnCodAndStatusNot(Long plnCod, EStatusAtividade status);

    boolean existsByPlnCodAndEmpCodAndOrigem(Long plnCod, Long empCod, EOrigemAtividade origem);

    boolean existsByPlnCodAndEmpCodAndRespPesCodNotNull(Long plnCod, Long empCod);

    boolean existsByPlnCodAndEmpCodAndPrazoNotNull(Long plnCod, Long empCod);

    boolean existsByPlnCodAndEmpCodAndStatusNot(Long plnCod, Long empCod, EStatusAtividade status);

    boolean existsByAgrcCod(Long agrcCod);
}

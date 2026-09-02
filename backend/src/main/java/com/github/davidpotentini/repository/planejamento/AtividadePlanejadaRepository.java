package com.github.davidpotentini.repository.planejamento;

import com.github.davidpotentini.enums.EOrigemAtividade;
import com.github.davidpotentini.enums.EStatusAtividade;
import com.github.davidpotentini.model.planejamento.AtividadePlanejadaModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AtividadePlanejadaRepository extends JpaRepository<AtividadePlanejadaModel, Long> {

    /** Atividades do planejamento, na ordem de exibição ({@code ordem}); agrupadas por prática no service. */
    List<AtividadePlanejadaModel> findByPlnCodOrderByOrdemAscAtpCodAsc(Long plnCod);

    /** Atividade de maior {@code ordem} na prática do plano — para anexar a complementar no fim. */
    Optional<AtividadePlanejadaModel> findFirstByPlnCodAndPrtcCodOrderByOrdemDesc(Long plnCod, Long prtcCod);

    /** Atividades de uma prática no plano — usado ao reordenar (arrastar-e-soltar). */
    List<AtividadePlanejadaModel> findByPlnCodAndPrtcCod(Long plnCod, Long prtcCod);

    /** Total de atividades do planejamento (denominador do progresso). */
    long countByPlnCod(Long plnCod);

    /** Atividades num dado estado de execução (numerador do progresso quando {@code CONCLUIDA}). */
    long countByPlnCodAndStatus(Long plnCod, EStatusAtividade status);

    // ---- detecção de edições no plano (trava para regerar o ciclo) ----

    /** Existe atividade de uma origem (ex.: {@code COMPLEMENTAR} = incluída pelo usuário). */
    boolean existsByPlnCodAndOrigem(Long plnCod, EOrigemAtividade origem);

    /** Existe atividade com responsável definido (ajuste do usuário). */
    boolean existsByPlnCodAndRespPesCodNotNull(Long plnCod);

    /** Existe atividade com prazo definido (ajuste do usuário). */
    boolean existsByPlnCodAndPrazoNotNull(Long plnCod);

    /** Existe atividade fora do estado inicial (execução iniciada). */
    boolean existsByPlnCodAndStatusNot(Long plnCod, EStatusAtividade status);
}

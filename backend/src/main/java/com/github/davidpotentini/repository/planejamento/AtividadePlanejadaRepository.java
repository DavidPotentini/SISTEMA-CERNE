package com.github.davidpotentini.repository.planejamento;

import com.github.davidpotentini.enums.EStatusAtividade;
import com.github.davidpotentini.model.planejamento.AtividadePlanejadaModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AtividadePlanejadaRepository extends JpaRepository<AtividadePlanejadaModel, Long> {

    /** Atividades do planejamento (agrupadas por prática no service). */
    List<AtividadePlanejadaModel> findByPlnCodOrderByAtpCodAsc(Long plnCod);

    /** Total de atividades do planejamento (denominador do progresso). */
    long countByPlnCod(Long plnCod);

    /** Atividades num dado estado de execução (numerador do progresso quando {@code CONCLUIDA}). */
    long countByPlnCodAndStatus(Long plnCod, EStatusAtividade status);
}

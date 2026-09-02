package com.github.davidpotentini.repository.evidencia;

import com.github.davidpotentini.model.evidencia.EvidenciaId;
import com.github.davidpotentini.model.evidencia.EvidenciaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EvidenciaRepository extends JpaRepository<EvidenciaModel, EvidenciaId> {

    /** Há alguma evidência vinculada à atividade? (trava para excluir a atividade planejada). */
    boolean existsByAtpCod(Long atpCod);

    /** Há alguma evidência em qualquer atividade do planejamento? (trava para regerar o ciclo). */
    @Query(value = "SELECT COUNT(*) > 0 FROM EVIDENCIAS WHERE ATP_COD IN "
            + "(SELECT ATP_COD FROM ATIVIDADES_PLANEJADAS WHERE PLN_COD = :plnCod)", nativeQuery = true)
    boolean existsByPlanejamento(@Param("plnCod") Long plnCod);

    /** Próximo id lógico de evidência (nova evidência = nova raiz). */
    @Query(value = "SELECT nextval('SEQ_EVIDENCIA')", nativeQuery = true)
    Long proximoEvdCod();

    /** Maior versão já registrada para a evidência (0 se não houver). */
    @Query(value = "SELECT COALESCE(MAX(EVD_COD_SEQ), 0) FROM EVIDENCIAS WHERE EVD_COD = :evdCod",
            nativeQuery = true)
    int ultimaSeq(@Param("evdCod") Long evdCod);

    /** Histórico da evidência: todas as versões em ordem crescente (ABRIR). */
    @Query(value = "SELECT * FROM EVIDENCIAS WHERE EVD_COD = :evdCod ORDER BY EVD_COD_SEQ",
            nativeQuery = true)
    List<EvidenciaModel> historico(@Param("evdCod") Long evdCod);

    /** Versão corrente da evidência (maior SEQ). */
    @Query(value = "SELECT * FROM EVIDENCIAS WHERE EVD_COD = :evdCod ORDER BY EVD_COD_SEQ DESC LIMIT 1",
            nativeQuery = true)
    Optional<EvidenciaModel> versaoCorrente(@Param("evdCod") Long evdCod);

    /** Uma linha por evidência: a versão corrente (maior SEQ) de cada id lógico — para a listagem. */
    @Query(value = """
            SELECT * FROM EVIDENCIAS
            WHERE EVD_COD_SEQ = (SELECT MAX(EVD_COD_SEQ) FROM EVIDENCIAS max_versao
                                 WHERE max_versao.EVD_COD = EVIDENCIAS.EVD_COD)
            ORDER BY EVD_COD DESC
            """, nativeQuery = true)
    List<EvidenciaModel> versoesCorrentes();

    /** Versões correntes vinculadas a uma atividade — base da regra de conclusão (todas VALIDADA). */
    @Query(value = """
            SELECT * FROM EVIDENCIAS
            WHERE ATP_COD = :atpCod
              AND EVD_COD_SEQ = (SELECT MAX(EVD_COD_SEQ) FROM EVIDENCIAS max_versao
                                 WHERE max_versao.EVD_COD = EVIDENCIAS.EVD_COD)
            ORDER BY EVD_COD DESC
            """, nativeQuery = true)
    List<EvidenciaModel> versoesCorrentesDaAtividade(@Param("atpCod") Long atpCod);
}

package com.github.davidpotentini.repository.incubadoras;

import com.github.davidpotentini.enums.EStatusIncubadora;
import com.github.davidpotentini.model.incubadoras.IncubadorasModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IncubadorasRepository extends JpaRepository<IncubadorasModel, Long> {

    Optional<IncubadorasModel> findFirstByNome(String nome);

    /** Colunas (mapeadas posicionalmente no service): INC_COD, NOME, MANTENEDORA, responsável, qtd. usuários, STATUS. */
    @Query(value = """
            SELECT
                INCUBADORAS.INC_COD,
                INCUBADORAS.NOME,
                INCUBADORAS.MANTENEDORA,
                (SELECT CONTAS.NOME FROM CONTAS WHERE CONTAS.CTA_COD = INCUBADORAS.RESP_CTA_COD),
                (SELECT COUNT(*) FROM CONTAS WHERE CONTAS.INC_COD = INCUBADORAS.INC_COD),
                CAST(INCUBADORAS.STATUS AS TEXT)
            FROM INCUBADORAS
            WHERE (CAST(:nome AS TEXT) IS NULL OR INCUBADORAS.NOME ILIKE '%' || CAST(:nome AS TEXT) || '%')
              AND (CAST(:status AS TEXT) IS NULL OR CAST(INCUBADORAS.STATUS AS TEXT) = CAST(:status AS TEXT))
            ORDER BY INCUBADORAS.NOME
            """, nativeQuery = true)
    List<Object[]> listarResumo(@Param("nome") String nome, @Param("status") String status);

    long countByStatus(EStatusIncubadora status);

    boolean existsByNomeSchema(String nomeSchema);
}

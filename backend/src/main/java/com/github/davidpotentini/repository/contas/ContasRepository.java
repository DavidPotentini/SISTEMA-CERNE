package com.github.davidpotentini.repository.contas;
import com.github.davidpotentini.model.contas.ContasModel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ContasRepository extends JpaRepository<ContasModel, Long> {

    Optional<ContasModel> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query(value = """
            SELECT *
              FROM CONTAS
             WHERE CONTAS.ADMIN_PLATAFORMA = FALSE
             ORDER BY CONTAS.NOME ASC
            """,
           nativeQuery = true)
    List<ContasModel> findAllByOrderByNomeAsc();
}

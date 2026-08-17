package com.github.davidpotentini.repository.contas;
import com.github.davidpotentini.model.contas.ContasModel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ContasRepository extends JpaRepository<ContasModel, Long> {

    /** Busca a conta pela identidade de login. Usada no {@code POST /login}. */
    Optional<ContasModel> findByEmail(String email);

    /** E-mail já cadastrado (identidade de login é única). Usado no convite. */
    boolean existsByEmail(String email);

    /** Todas as contas da plataforma (Menos Administrador), ordenadas por nome — listagem de usuários. */
    @Query(value = """
            SELECT *
              FROM CONTAS
             WHERE CONTAS.ADMIN_PLATAFORMA = FALSE
             ORDER BY CONTAS.NOME ASC
            """,
           nativeQuery = true)
    List<ContasModel> findAllByOrderByNomeAsc();
}

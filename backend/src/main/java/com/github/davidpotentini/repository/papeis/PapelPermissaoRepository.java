package com.github.davidpotentini.repository.papeis;

import com.github.davidpotentini.enums.ERecurso;
import com.github.davidpotentini.model.papeis.PapelPermissaoModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PapelPermissaoRepository extends JpaRepository<PapelPermissaoModel, Long> {

    /** Todas as permissões de um papel (para montar a matriz do login). */
    List<PapelPermissaoModel> findByPapCod(Long papCod);

    /** Nível do papel num recurso específico (checagem do interceptor). */
    Optional<PapelPermissaoModel> findByPapCodAndRecurso(Long papCod, ERecurso recurso);
}

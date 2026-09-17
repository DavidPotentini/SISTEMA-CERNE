package com.github.davidpotentini.repository.papeis;

import com.github.davidpotentini.enums.ERecurso;
import com.github.davidpotentini.model.papeis.PapelPermissaoModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PapelPermissaoRepository extends JpaRepository<PapelPermissaoModel, Long> {

    List<PapelPermissaoModel> findByPapCod(Long papCod);

    Optional<PapelPermissaoModel> findByPapCodAndRecurso(Long papCod, ERecurso recurso);
}

package com.github.davidpotentini.cerne2.repository.pessoas;

import com.github.davidpotentini.cerne2.enums.ETipoEmpreendimento;
import com.github.davidpotentini.cerne2.models.pessoas.PessoasModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PessoasRepository extends JpaRepository <PessoasModel, Long> {

    Optional<List<PessoasModel>> findByTipoEmpreendimento(ETipoEmpreendimento tipoEmpreendimento);

    Optional<List<PessoasModel>> findByIncubadasModel_IncCod(Long incCod);
}

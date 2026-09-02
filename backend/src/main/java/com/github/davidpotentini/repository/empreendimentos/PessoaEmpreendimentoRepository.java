package com.github.davidpotentini.repository.empreendimentos;

import com.github.davidpotentini.model.empreendimentos.PessoaEmpreendimentoModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PessoaEmpreendimentoRepository
        extends JpaRepository<PessoaEmpreendimentoModel, Long> {

    List<PessoaEmpreendimentoModel> findByEmpCodOrderByNomeAsc(Long empCod);

    Optional<PessoaEmpreendimentoModel> findByEmpCodAndRepresentanteLegalTrue(Long empCod);
}

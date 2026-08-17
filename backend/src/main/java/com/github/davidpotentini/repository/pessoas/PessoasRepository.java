package com.github.davidpotentini.repository.pessoas;
import com.github.davidpotentini.model.pessoas.PessoasModel;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PessoasRepository extends JpaRepository<PessoasModel, Long> {

    /** Pessoa deste tenant vinculada à conta global (ref. fraca a public.CONTAS). */
    Optional<PessoasModel> findByCtaCod(Long ctaCod);
}

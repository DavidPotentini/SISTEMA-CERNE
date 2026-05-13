package com.github.davidpotentini.cerne2.repository.informacoesgeraisincubadas;

import com.github.davidpotentini.cerne2.models.enderecos.EnderecosModel;
import com.github.davidpotentini.cerne2.models.informacoesgeraisincubadas.IncubadasModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface IncubadasRepository extends JpaRepository<IncubadasModel, Long> {

    Optional<EnderecosModel> findByEnderecosModel_EndCod(Long endCod);
}

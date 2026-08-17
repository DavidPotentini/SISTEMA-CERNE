package com.github.davidpotentini.repository.monitoramento;

import com.github.davidpotentini.model.monitoramento.PontuacaoId;
import com.github.davidpotentini.model.monitoramento.PontuacaoModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface PontuacaoRepository extends JpaRepository<PontuacaoModel, PontuacaoId> {

    List<PontuacaoModel> findByAvaCodIn(Collection<Long> avaCods);
}

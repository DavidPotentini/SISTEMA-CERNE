package com.github.davidpotentini.service.execucao;

import com.github.davidpotentini.comum.erro.NaoEncontradoException;
import com.github.davidpotentini.comum.erro.RegraNegocioException;
import com.github.davidpotentini.enums.EStatusAtividade;
import com.github.davidpotentini.enums.EStatusEvidencia;
import com.github.davidpotentini.model.evidencia.EvidenciaModel;
import com.github.davidpotentini.model.planejamento.AtividadePlanejadaModel;
import com.github.davidpotentini.repository.evidencia.EvidenciaRepository;
import com.github.davidpotentini.repository.planejamento.AtividadePlanejadaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Acompanhamento de execução: muda o estado de uma atividade planejada. Os estados definidos
 * manualmente são {@code PLANEJADA}, {@code EM_ANDAMENTO} e {@code CONCLUIDA} — {@code ATRASADA} é
 * derivado do prazo, nunca escolhido aqui. Concluir exige que a atividade tenha ao menos uma
 * evidência e que todas as versões correntes estejam {@code VALIDADA}.
 */
@Service
public class ExecucaoService {

    private final AtividadePlanejadaRepository atividades;
    private final EvidenciaRepository evidencias;

    public ExecucaoService(AtividadePlanejadaRepository atividades, EvidenciaRepository evidencias) {
        this.atividades = atividades;
        this.evidencias = evidencias;
    }

    @Transactional(rollbackFor = Exception.class)
    public void mudarStatus(Long atpCod, EStatusAtividade novo) {
        if (novo == EStatusAtividade.ATRASADA) {
            throw new RegraNegocioException("O status atrasada é definido pelo prazo, não manualmente.");
        }
        AtividadePlanejadaModel atividade = atividades.findById(atpCod)
                .orElseThrow(() -> new NaoEncontradoException("Atividade", atpCod));

        if (novo == EStatusAtividade.CONCLUIDA) {
            exigirEvidenciasValidadas(atpCod);
        }

        atividade.setStatus(novo);
        atividades.save(atividade);
    }

    /** Só conclui com ao menos uma evidência e todas as versões correntes validadas. */
    private void exigirEvidenciasValidadas(Long atpCod) {
        List<EvidenciaModel> correntes = evidencias.versoesCorrentesDaAtividade(atpCod);
        if (correntes.isEmpty()) {
            throw new RegraNegocioException("A atividade só pode ser concluída com ao menos uma evidência validada.");
        }
        boolean todasValidadas = correntes.stream()
                .allMatch(e -> e.getStatus() == EStatusEvidencia.VALIDADA);
        if (!todasValidadas) {
            throw new RegraNegocioException("Há evidências pendentes de validação nesta atividade.");
        }
    }
}

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

package com.github.davidpotentini.service.painel;

import com.github.davidpotentini.comum.ciclo.CicloContexto;
import com.github.davidpotentini.dto.indicador.PainelIndicadorDTO;
import com.github.davidpotentini.dto.painel.ProcessoFluxoDTO;
import com.github.davidpotentini.dto.painel.ResumoCicloDTO;
import com.github.davidpotentini.enums.EEstadoProcesso;
import com.github.davidpotentini.enums.EStatusAtividade;
import com.github.davidpotentini.enums.EStatusEvidencia;
import com.github.davidpotentini.enums.EStatusPlanejamento;
import com.github.davidpotentini.enums.EStatusEmpreendimento;
import com.github.davidpotentini.model.ciclos.CiclosModel;
import com.github.davidpotentini.model.empreendimentos.EmpreendimentosModel;
import com.github.davidpotentini.model.estruturaciclo.PraticaCicloModel;
import com.github.davidpotentini.model.estruturaciclo.ProcessoCicloModel;
import com.github.davidpotentini.model.evidencia.EvidenciaModel;
import com.github.davidpotentini.model.planejamento.AtividadePlanejadaModel;
import com.github.davidpotentini.model.planejamento.PlanejamentoModel;
import com.github.davidpotentini.repository.empreendimentos.EmpreendimentosRepository;
import com.github.davidpotentini.repository.estruturaciclo.PraticaCicloRepository;
import com.github.davidpotentini.repository.estruturaciclo.ProcessoCicloRepository;
import com.github.davidpotentini.repository.evidencia.EvidenciaRepository;
import com.github.davidpotentini.repository.planejamento.AtividadePlanejadaRepository;
import com.github.davidpotentini.repository.planejamento.PlanejamentoRepository;
import com.github.davidpotentini.service.indicador.ApuracaoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Resumo agregado do ciclo em foco. Reaproveita o escopo de Pendências (planejamento vigente + versões
 * correntes de evidência) e a regra de "meta atingida" da apuração ({@link ApuracaoService}), para não divergir.
 */
@Service
public class PainelVisaoGeralService {

    private final CicloContexto cicloContexto;
    private final PlanejamentoRepository planejamentos;
    private final AtividadePlanejadaRepository atividades;
    private final EmpreendimentosRepository empreendimentos;
    private final EvidenciaRepository evidencias;
    private final ProcessoCicloRepository processosCiclo;
    private final PraticaCicloRepository praticasCiclo;
    private final ApuracaoService apuracaoService;

    public PainelVisaoGeralService(CicloContexto cicloContexto, PlanejamentoRepository planejamentos,
                                   AtividadePlanejadaRepository atividades,
                                   EmpreendimentosRepository empreendimentos,
                                   EvidenciaRepository evidencias, ProcessoCicloRepository processosCiclo,
                                   PraticaCicloRepository praticasCiclo, ApuracaoService apuracaoService) {
        this.cicloContexto = cicloContexto;
        this.planejamentos = planejamentos;
        this.atividades = atividades;
        this.empreendimentos = empreendimentos;
        this.evidencias = evidencias;
        this.processosCiclo = processosCiclo;
        this.praticasCiclo = praticasCiclo;
        this.apuracaoService = apuracaoService;
    }

    @Transactional(readOnly = true)
    public ResumoCicloDTO resumo() {
        CiclosModel ciclo = cicloContexto.emFoco();
        if (ciclo == null) {
            return new ResumoCicloDTO(null, 0, 0, 0, 0, 0, 0, 0, 0, 0 ,List.of());
        }

        List<AtividadePlanejadaModel> ativs = planejamentos
                .findByCicCodAndStatus(ciclo.getCicCod(), EStatusPlanejamento.PUBLICADO)
                .map(PlanejamentoModel::getPlnCod)
                .map(atividades::findByPlnCodOrderByOrdemAscAtpCodAsc)
                .orElse(List.of());

        long atividadesTotal = ativs.size();
        long atividadesConcluidas = 0;
        Set<Long> atpCods = new HashSet<>();
        // Contagem [concluidas, total] por prática do ciclo, para montar o fluxo depois.
        Map<Long, long[]> contagemPorPratica = new HashMap<>();
        for (AtividadePlanejadaModel atv : ativs) {
            atpCods.add(atv.getAtpCod());
            boolean concluida = atv.getStatus() == EStatusAtividade.CONCLUIDA;
            if (concluida) {
                atividadesConcluidas++;
            }
            long[] c = contagemPorPratica.get(atv.getPrtcCod());
            if (c == null) {
                c = new long[2];
                contagemPorPratica.put(atv.getPrtcCod(), c);
            }
            if (concluida) {
                c[0]++;
            }
            c[1]++;
        }
        int progresso = atividadesTotal == 0
                ? 0
                : (int) Math.round(atividadesConcluidas * 100.0 / atividadesTotal);

        long empreendimentosAtivos = 0;
        for (EmpreendimentosModel emp : empreendimentos.findAllByOrderByNomeAsc()) {
            if (emp.getStatus() == EStatusEmpreendimento.ATIVO) {
                empreendimentosAtivos++;
            }
        }

        long evidenciasRegistradas = 0;
        long evidenciasValidadas = 0;
        for (EvidenciaModel ev : evidencias.versoesCorrentes()) {
            if (!atpCods.contains(ev.getAtpCod())) {
                continue;
            }
            evidenciasRegistradas++;
            if (ev.getStatus() == EStatusEvidencia.VALIDADA) {
                evidenciasValidadas++;
            }
        }

        List<PainelIndicadorDTO> painelIndicadores = apuracaoService.painel();
        long indicadoresTotal = painelIndicadores.size();
        long indicadoresComMeta = 0;
        long indicadoresAtingidos = 0;
        for (PainelIndicadorDTO ind : painelIndicadores) {
            if (!ind.temMeta()) {
                continue;
            }
            indicadoresComMeta++;
            if (ind.atingido()) {
                indicadoresAtingidos++;
            }
        }

        List<ProcessoFluxoDTO> fluxo = montarFluxo(ciclo.getCicCod(), contagemPorPratica);

        return new ResumoCicloDTO(ciclo.getNome(), atividadesConcluidas, atividadesTotal, progresso,
                empreendimentosAtivos, evidenciasRegistradas, evidenciasValidadas,
                indicadoresAtingidos, indicadoresComMeta, indicadoresTotal, fluxo);
    }

    private List<ProcessoFluxoDTO> montarFluxo(Long cicCod, Map<Long, long[]> contagemPorPratica) {
        List<ProcessoFluxoDTO> fluxo = new ArrayList<>();
        for (ProcessoCicloModel proc : processosCiclo.findByCicCodOrderByOrdemAscPrccCodAsc(cicCod)) {
            long concluidas = 0;
            long total = 0;
            for (PraticaCicloModel pr : praticasCiclo.findByPrccCodOrderByOrdemAscPrtcCodAsc(proc.getPrccCod())) {
                long[] c = contagemPorPratica.get(pr.getPrtcCod());
                if (c != null) {
                    concluidas += c[0];
                    total += c[1];
                }
            }

            EEstadoProcesso estado;
            if (total > 0 && concluidas == total) {
                estado = EEstadoProcesso.CONCLUIDO;
            } else if (concluidas > 0) {
                estado = EEstadoProcesso.EM_ANDAMENTO;
            } else {
                estado = EEstadoProcesso.NAO_INICIADO;
            }

            int ordem = proc.getOrdem() != null ? proc.getOrdem() : 0;
            fluxo.add(new ProcessoFluxoDTO(proc.getPrccCod(), proc.getNome(), ordem, estado,
                    concluidas, total));
        }
        return fluxo;
    }
}

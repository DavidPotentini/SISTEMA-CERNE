package com.github.davidpotentini.service.painel;

import com.github.davidpotentini.dto.indicador.IndicadorCicloDTO;
import com.github.davidpotentini.dto.painel.PendenciaDTO;
import com.github.davidpotentini.enums.EStatusAtividade;
import com.github.davidpotentini.enums.EStatusCiclo;
import com.github.davidpotentini.enums.EStatusEvidencia;
import com.github.davidpotentini.enums.EStatusPlanejamento;
import com.github.davidpotentini.enums.ETipoPendencia;
import com.github.davidpotentini.model.ciclos.CiclosModel;
import com.github.davidpotentini.model.empreendimentos.EmpreendimentosModel;
import com.github.davidpotentini.model.evidencia.EvidenciaModel;
import com.github.davidpotentini.model.indicador.MetaModel;
import com.github.davidpotentini.model.indicador.ResultadoModel;
import com.github.davidpotentini.model.metodologia.PraticaModel;
import com.github.davidpotentini.model.metodologia.ProcessoModel;
import com.github.davidpotentini.model.planejamento.AtividadePlanejadaModel;
import com.github.davidpotentini.model.planejamento.PlanejamentoModel;
import com.github.davidpotentini.repository.ciclos.CiclosRepository;
import com.github.davidpotentini.repository.empreendimentos.EmpreendimentosRepository;
import com.github.davidpotentini.repository.evidencia.EvidenciaRepository;
import com.github.davidpotentini.repository.indicador.MetaRepository;
import com.github.davidpotentini.repository.indicador.ResultadoRepository;
import com.github.davidpotentini.repository.metodologia.PraticaRepository;
import com.github.davidpotentini.repository.metodologia.ProcessoRepository;
import com.github.davidpotentini.repository.planejamento.AtividadePlanejadaRepository;
import com.github.davidpotentini.repository.planejamento.PlanejamentoRepository;
import com.github.davidpotentini.service.indicador.IndicadorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Painel Operacional do ciclo ativo (schema do tenant vem do JWT): reúne todas as pendências numa
 * lista única para o front agrupar em seções. Quatro origens: atividades em aberto e atrasadas
 * (planejamento vigente), evidências com correção solicitada (versão corrente) e metas de indicadores
 * já vencidas sem resultado. Cada item carrega o processo/prática CERNE a que pertence. Reaproveita
 * {@link IndicadorService} para os rótulos dos indicadores.
 */
@Service
public class PainelOperacionalService {

    private final CiclosRepository ciclos;
    private final PlanejamentoRepository planejamentos;
    private final AtividadePlanejadaRepository atividades;
    private final PraticaRepository praticas;
    private final ProcessoRepository processos;
    private final EmpreendimentosRepository empreendimentos;
    private final EvidenciaRepository evidencias;
    private final IndicadorService indicadorService;
    private final MetaRepository metas;
    private final ResultadoRepository resultados;

    public PainelOperacionalService(CiclosRepository ciclos, PlanejamentoRepository planejamentos,
                                    AtividadePlanejadaRepository atividades, PraticaRepository praticas,
                                    ProcessoRepository processos,
                                    EmpreendimentosRepository empreendimentos,
                                    EvidenciaRepository evidencias, IndicadorService indicadorService,
                                    MetaRepository metas, ResultadoRepository resultados) {
        this.ciclos = ciclos;
        this.planejamentos = planejamentos;
        this.atividades = atividades;
        this.praticas = praticas;
        this.processos = processos;
        this.empreendimentos = empreendimentos;
        this.evidencias = evidencias;
        this.indicadorService = indicadorService;
        this.metas = metas;
        this.resultados = resultados;
    }

    /** Todas as pendências do ciclo ativo, numa lista única (o front agrupa por {@code tipo}). */
    @Transactional(readOnly = true)
    public List<PendenciaDTO> pendencias() {
        List<PendenciaDTO> pendencias = new ArrayList<>();
        CiclosModel ciclo = cicloAtivo();
        if (ciclo == null) {
            return pendencias;
        }

        // Atividades do planejamento vigente + índice atpCod → prtCod (para as evidências).
        List<AtividadePlanejadaModel> ativs = planejamentos
                .findByCicCodAndStatus(ciclo.getCicCod(), EStatusPlanejamento.PUBLICADO)
                .map(PlanejamentoModel::getPlnCod)
                .map(atividades::findByPlnCodOrderByAtpCodAsc)
                .orElse(List.of());
        Map<Long, Long> prtPorAtp = new HashMap<>();
        for (AtividadePlanejadaModel atv : ativs) {
            prtPorAtp.put(atv.getAtpCod(), atv.getPrtCod());
        }

        // Rótulos processo/prática por prtCod (em lote).
        Set<Long> prtCods = new HashSet<>(prtPorAtp.values());
        Map<Long, String> praticaNomePorPrt = new HashMap<>();
        Map<Long, Long> prcPorPrt = new HashMap<>();
        Set<Long> prcCods = new HashSet<>();
        for (PraticaModel pr : praticas.findAllById(prtCods)) {
            praticaNomePorPrt.put(pr.getPrtCod(), pr.getNome());
            prcPorPrt.put(pr.getPrtCod(), pr.getPrcCod());
            prcCods.add(pr.getPrcCod());
        }
        Map<Long, String> processoNomePorPrc = new HashMap<>();
        for (ProcessoModel proc : processos.findAllById(prcCods)) {
            processoNomePorPrc.put(proc.getPrcCod(), proc.getNome());
        }

        pendenciasDeAtividades(ativs, praticaNomePorPrt, prcPorPrt, processoNomePorPrc, pendencias);
        pendenciasDeEvidencias(prtPorAtp, praticaNomePorPrt, prcPorPrt, processoNomePorPrc, pendencias);
        pendenciasDeMetas(pendencias);
        return pendencias;
    }

    // ---- atividades (em aberto / atrasadas) ----

    private void pendenciasDeAtividades(List<AtividadePlanejadaModel> ativs,
                                        Map<Long, String> praticaNomePorPrt, Map<Long, Long> prcPorPrt,
                                        Map<Long, String> processoNomePorPrc,
                                        List<PendenciaDTO> pendencias) {
        Map<Long, String> nomeEmp = nomesEmpreendimentos(ativs);
        LocalDate hoje = LocalDate.now();

        for (AtividadePlanejadaModel atv : ativs) {
            if (atv.getStatus() == EStatusAtividade.CONCLUIDA) {
                continue;
            }
            boolean atrasada = atv.getStatus() == EStatusAtividade.ATRASADA
                    || (atv.getPrazo() != null && atv.getPrazo().isBefore(hoje));
            String detalhe = atv.getEmpCod() == null ? null : nomeEmp.get(atv.getEmpCod());
            pendencias.add(new PendenciaDTO(
                    atrasada ? ETipoPendencia.ATIVIDADE_ATRASADA : ETipoPendencia.ATIVIDADE_ABERTA,
                    atv.getAtpCod(), atv.getNome(),
                    processoNome(atv.getPrtCod(), prcPorPrt, processoNomePorPrc),
                    praticaNomePorPrt.get(atv.getPrtCod()),
                    detalhe, atv.getPrazo(), atv.getRespPesCod()));
        }
    }

    private Map<Long, String> nomesEmpreendimentos(List<AtividadePlanejadaModel> lista) {
        Set<Long> empCods = new HashSet<>();
        for (AtividadePlanejadaModel atv : lista) {
            if (atv.getEmpCod() != null) {
                empCods.add(atv.getEmpCod());
            }
        }
        Map<Long, String> nomes = new HashMap<>();
        for (EmpreendimentosModel emp : empreendimentos.findAllById(empCods)) {
            nomes.put(emp.getEmpCod(), emp.getNome());
        }
        return nomes;
    }

    // ---- evidências com correção solicitada ----

    private void pendenciasDeEvidencias(Map<Long, Long> prtPorAtp, Map<Long, String> praticaNomePorPrt,
                                        Map<Long, Long> prcPorPrt, Map<Long, String> processoNomePorPrc,
                                        List<PendenciaDTO> pendencias) {
        for (EvidenciaModel ev : evidencias.versoesCorrentes()) {
            if (ev.getStatus() != EStatusEvidencia.CORRECAO_SOLICITADA) {
                continue;
            }
            // Só as do planejamento vigente do ciclo ativo (atividade presente no índice).
            Long prtCod = prtPorAtp.get(ev.getAtpCod());
            if (prtCod == null) {
                continue;
            }
            pendencias.add(new PendenciaDTO(
                    ETipoPendencia.EVIDENCIA_CORRECAO,
                    ev.getEvdCod(), ev.getTitulo(),
                    processoNome(prtCod, prcPorPrt, processoNomePorPrc),
                    praticaNomePorPrt.get(prtCod),
                    ev.getMotivoCorrecao(), null, ev.getRegPesCod()));
        }
    }

    // ---- metas de indicadores vencidas (sem resultado) ----

    private void pendenciasDeMetas(List<PendenciaDTO> pendencias) {
        List<IndicadorCicloDTO> indicadores = indicadorService.listar();
        if (indicadores.isEmpty()) {
            return;
        }
        Map<Long, IndicadorCicloDTO> porInd = new HashMap<>();
        List<Long> indCods = new ArrayList<>();
        for (IndicadorCicloDTO ind : indicadores) {
            porInd.put(ind.indCod(), ind);
            indCods.add(ind.indCod());
        }

        List<MetaModel> todasMetas = metas.findByIndCodIn(indCods);
        List<Long> metCods = new ArrayList<>();
        for (MetaModel m : todasMetas) {
            metCods.add(m.getMetCod());
        }
        Set<Long> apurados = new HashSet<>();
        for (ResultadoModel r : resultados.findByMetCodIn(metCods)) {
            apurados.add(r.getMetCod());
        }

        LocalDate hoje = LocalDate.now();
        for (MetaModel m : todasMetas) {
            boolean vencida = m.getDataFimApuracao() != null && m.getDataFimApuracao().isBefore(hoje);
            if (!vencida || apurados.contains(m.getMetCod())) {
                continue;
            }
            IndicadorCicloDTO ind = porInd.get(m.getIndCod());
            if (ind == null) {
                continue;
            }
            pendencias.add(new PendenciaDTO(
                    ETipoPendencia.META_VENCIDA,
                    ind.indCod(), ind.nome(),
                    ind.processoNome(), ind.praticaNome(),
                    null, m.getDataFimApuracao(), ind.respPesCod()));
        }
    }

    // ---- apoio ----

    /** Nome do processo da prática {@code prtCod}, resolvido pelos índices em lote (ou {@code null}). */
    private String processoNome(Long prtCod, Map<Long, Long> prcPorPrt,
                                Map<Long, String> processoNomePorPrc) {
        Long prcCod = prcPorPrt.get(prtCod);
        return prcCod == null ? null : processoNomePorPrc.get(prcCod);
    }

    private CiclosModel cicloAtivo() {
        List<CiclosModel> ativos = ciclos.findByStatus(EStatusCiclo.ATIVO);
        return ativos.isEmpty() ? null : ativos.get(0);
    }
}

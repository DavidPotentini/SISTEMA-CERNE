package com.github.davidpotentini.service.painel;

import com.github.davidpotentini.dto.indicador.IndicadorCicloDTO;
import com.github.davidpotentini.dto.painel.PendenciaDTO;
import com.github.davidpotentini.enums.EStatusAtividade;
import com.github.davidpotentini.enums.EStatusEvidencia;
import com.github.davidpotentini.enums.EStatusPlanejamento;
import com.github.davidpotentini.enums.ETipoPendencia;
import com.github.davidpotentini.comum.ciclo.CicloContexto;
import com.github.davidpotentini.model.ciclos.CiclosModel;
import com.github.davidpotentini.model.empreendimentos.EmpreendimentosModel;
import com.github.davidpotentini.model.estruturaciclo.PraticaCicloModel;
import com.github.davidpotentini.model.estruturaciclo.ProcessoCicloModel;
import com.github.davidpotentini.model.evidencia.EvidenciaModel;
import com.github.davidpotentini.model.indicador.MetaModel;
import com.github.davidpotentini.model.indicador.ResultadoModel;
import com.github.davidpotentini.model.planejamento.AtividadePlanejadaModel;
import com.github.davidpotentini.model.planejamento.PlanejamentoModel;
import com.github.davidpotentini.repository.empreendimentos.EmpreendimentosRepository;
import com.github.davidpotentini.repository.estruturaciclo.PraticaCicloRepository;
import com.github.davidpotentini.repository.estruturaciclo.ProcessoCicloRepository;
import com.github.davidpotentini.repository.evidencia.EvidenciaRepository;
import com.github.davidpotentini.repository.indicador.MetaRepository;
import com.github.davidpotentini.repository.indicador.ResultadoRepository;
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
 * Reúne as pendências do ciclo em foco numa lista única. Quatro origens: atividades em aberto e
 * atrasadas (planejamento vigente), evidências em correção (versão corrente) e metas vencidas sem resultado.
 */
@Service
public class PendenciasService {

    private final CicloContexto cicloContexto;
    private final PlanejamentoRepository planejamentos;
    private final AtividadePlanejadaRepository atividades;
    private final PraticaCicloRepository praticasCiclo;
    private final ProcessoCicloRepository processosCiclo;
    private final EmpreendimentosRepository empreendimentos;
    private final EvidenciaRepository evidencias;
    private final IndicadorService indicadorService;
    private final MetaRepository metas;
    private final ResultadoRepository resultados;

    public PendenciasService(CicloContexto cicloContexto, PlanejamentoRepository planejamentos,
                                    AtividadePlanejadaRepository atividades,
                                    PraticaCicloRepository praticasCiclo,
                                    ProcessoCicloRepository processosCiclo,
                                    EmpreendimentosRepository empreendimentos,
                                    EvidenciaRepository evidencias, IndicadorService indicadorService,
                                    MetaRepository metas, ResultadoRepository resultados) {
        this.cicloContexto = cicloContexto;
        this.planejamentos = planejamentos;
        this.atividades = atividades;
        this.praticasCiclo = praticasCiclo;
        this.processosCiclo = processosCiclo;
        this.empreendimentos = empreendimentos;
        this.evidencias = evidencias;
        this.indicadorService = indicadorService;
        this.metas = metas;
        this.resultados = resultados;
    }

    @Transactional(readOnly = true)
    public List<PendenciaDTO> pendencias() {
        List<PendenciaDTO> pendencias = new ArrayList<>();
        CiclosModel ciclo = cicloContexto.emFoco();
        if (ciclo == null) {
            return pendencias;
        }

        // Atividades do planejamento vigente + índice atpCod → prtCod (para as evidências).
        List<AtividadePlanejadaModel> ativs = planejamentos
                .findByCicCodAndStatus(ciclo.getCicCod(), EStatusPlanejamento.PUBLICADO)
                .map(PlanejamentoModel::getPlnCod)
                .map(atividades::findByPlnCodOrderByOrdemAscAtpCodAsc)
                .orElse(List.of());
        Map<Long, Long> prtPorAtp = new HashMap<>();
        for (AtividadePlanejadaModel atv : ativs) {
            prtPorAtp.put(atv.getAtpCod(), atv.getPrtcCod());
        }

        // Rótulos processo/prática por prtcCod (instância do ciclo, em lote).
        Set<Long> prtcCods = new HashSet<>(prtPorAtp.values());
        Map<Long, String> praticaNomePorPrt = new HashMap<>();
        Map<Long, Long> prcPorPrt = new HashMap<>();
        Set<Long> prccCods = new HashSet<>();
        for (PraticaCicloModel pr : praticasCiclo.findAllById(prtcCods)) {
            praticaNomePorPrt.put(pr.getPrtcCod(), pr.getNome());
            prcPorPrt.put(pr.getPrtcCod(), pr.getPrccCod());
            prccCods.add(pr.getPrccCod());
        }
        Map<Long, String> processoNomePorPrc = new HashMap<>();
        for (ProcessoCicloModel proc : processosCiclo.findAllById(prccCods)) {
            processoNomePorPrc.put(proc.getPrccCod(), proc.getNome());
        }

        pendenciasDeAtividades(ativs, praticaNomePorPrt, prcPorPrt, processoNomePorPrc, pendencias);
        pendenciasDeEvidencias(prtPorAtp, praticaNomePorPrt, prcPorPrt, processoNomePorPrc, pendencias);
        pendenciasDeMetas(pendencias);
        return pendencias;
    }

    /**
     * Impedimentos ao encerramento: atividades não concluídas, evidências em correção e as metas ainda
     * sem resultado — aqui TODAS, não só as vencidas: encerrar exige o ciclo integralmente apurado.
     */
    @Transactional(readOnly = true)
    public long impedimentosDeEncerramento() {
        long total = 0;
        for (PendenciaDTO p : pendencias()) {
            if (p.tipo() != ETipoPendencia.META_VENCIDA) {
                total++;
            }
        }
        return total + metasSemResultado();
    }

    private long metasSemResultado() {
        List<IndicadorCicloDTO> indicadores = indicadorService.listar();
        if (indicadores.isEmpty()) {
            return 0;
        }
        List<Long> indCods = new ArrayList<>();
        for (IndicadorCicloDTO ind : indicadores) {
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
        long semResultado = 0;
        for (MetaModel m : todasMetas) {
            if (!apurados.contains(m.getMetCod())) {
                semResultado++;
            }
        }
        return semResultado;
    }


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
                    processoNome(atv.getPrtcCod(), prcPorPrt, processoNomePorPrc),
                    praticaNomePorPrt.get(atv.getPrtcCod()),
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


    private String processoNome(Long prtCod, Map<Long, Long> prcPorPrt,
                                Map<Long, String> processoNomePorPrc) {
        Long prcCod = prcPorPrt.get(prtCod);
        return prcCod == null ? null : processoNomePorPrc.get(prcCod);
    }
}

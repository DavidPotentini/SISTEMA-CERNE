package com.github.davidpotentini.service.indicador;

import com.github.davidpotentini.comum.erro.NaoEncontradoException;
import com.github.davidpotentini.comum.erro.RegraNegocioException;
import com.github.davidpotentini.dto.indicador.IndicadorCicloDTO;
import com.github.davidpotentini.dto.indicador.PraticaOpcaoDTO;
import com.github.davidpotentini.enums.EAtivoInativo;
import com.github.davidpotentini.enums.EOrigemIndicador;
import com.github.davidpotentini.enums.EStatusCiclo;
import com.github.davidpotentini.enums.ESituacaoVersao;
import com.github.davidpotentini.mapper.indicador.IndicadorMapper;
import com.github.davidpotentini.model.ciclos.CiclosModel;
import com.github.davidpotentini.model.indicador.IndicadorModel;
import com.github.davidpotentini.model.metodologia.IndicadorMetodologiaModel;
import com.github.davidpotentini.model.metodologia.PraticaModel;
import com.github.davidpotentini.model.metodologia.ProcessoModel;
import com.github.davidpotentini.model.metodologia.VersaoMetodologiaModel;
import com.github.davidpotentini.repository.ciclos.CiclosRepository;
import com.github.davidpotentini.repository.indicador.IndicadorRepository;
import com.github.davidpotentini.repository.metodologia.IndicadorMetodologiaRepository;
import com.github.davidpotentini.repository.metodologia.PraticaRepository;
import com.github.davidpotentini.repository.metodologia.ProcessoRepository;
import com.github.davidpotentini.repository.metodologia.VersaoMetodologiaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Indicadores do ciclo ativo (schema do tenant vem do JWT). "Gerar indicadores do ciclo" copia os
 * indicadores ATIVOS da metodologia vigente (origem {@code METODOLOGIA_CERNE}), substituindo os
 * gerados anteriormente e preservando os {@code COMPLEMENTAR}. "Definir complementar" inclui um
 * indicador manual. O "Vínculo CERNE" (processo/prática) é resolvido a partir do {@code PRT_COD}.
 */
@Service
public class IndicadorService {

    private final IndicadorRepository indicadores;
    private final VersaoMetodologiaRepository versoes;
    private final ProcessoRepository processos;
    private final PraticaRepository praticas;
    private final IndicadorMetodologiaRepository indicadoresMetodologia;
    private final CiclosRepository ciclos;
    private final IndicadorMapper mapper;

    public IndicadorService(IndicadorRepository indicadores, VersaoMetodologiaRepository versoes,
                            ProcessoRepository processos, PraticaRepository praticas,
                            IndicadorMetodologiaRepository indicadoresMetodologia,
                            CiclosRepository ciclos, IndicadorMapper mapper) {
        this.indicadores = indicadores;
        this.versoes = versoes;
        this.processos = processos;
        this.praticas = praticas;
        this.indicadoresMetodologia = indicadoresMetodologia;
        this.ciclos = ciclos;
        this.mapper = mapper;
    }

    /** Indicadores do ciclo ativo (vazio se não houver ciclo). */
    @Transactional(readOnly = true)
    public List<IndicadorCicloDTO> listar() {
        CiclosModel ciclo = cicloAtivo();
        if (ciclo == null) {
            return List.of();
        }
        return comLabels(indicadores.findByCicCodOrderByNomeAsc(ciclo.getCicCod()));
    }

    /**
     * Gera os indicadores do ciclo a partir da metodologia vigente: copia os indicadores ATIVOS das
     * práticas ATIVAS, com origem {@code METODOLOGIA_CERNE}. Substitui os gerados anteriormente (os
     * complementares permanecem).
     */
    @Transactional(rollbackFor = Exception.class)
    public List<IndicadorCicloDTO> gerarDoCiclo() {
        CiclosModel ciclo = cicloAtivoObrigatorio();
        VersaoMetodologiaModel vigente = versoes
                .findFirstBySituacaoOrderByVerCodDesc(ESituacaoVersao.VIGENTE)
                .orElseThrow(() -> new RegraNegocioException(
                        "Publique uma versão da metodologia antes de gerar os indicadores."));

        indicadores.deleteByCicCodAndOrigem(ciclo.getCicCod(), EOrigemIndicador.METODOLOGIA_CERNE);

        Set<Long> praticasAtivas = praticasAtivasDaVersao(vigente.getVerCod());
        if (!praticasAtivas.isEmpty()) {
            for (IndicadorMetodologiaModel base
                    : indicadoresMetodologia.findByPrtCodInOrderByNomeAsc(praticasAtivas)) {
                if (base.getSituacao() != EAtivoInativo.ATIVO) {
                    continue;
                }
                IndicadorModel ind = new IndicadorModel();
                ind.setNome(base.getNome());
                ind.setOrigem(EOrigemIndicador.METODOLOGIA_CERNE);
                ind.setPrtCod(base.getPrtCod());
                ind.setCicCod(ciclo.getCicCod());
                ind.setUnidade(base.getUnidade());
                ind.setPeriodicidade(base.getPeriodicidade());
                ind.setSituacao(EAtivoInativo.ATIVO);
                indicadores.save(ind);
            }
        }
        return comLabels(indicadores.findByCicCodOrderByNomeAsc(ciclo.getCicCod()));
    }

    /** Inclui um indicador complementar no ciclo ativo. O vínculo (prática) é opcional. */
    @Transactional(rollbackFor = Exception.class)
    public IndicadorCicloDTO definirComplementar(IndicadorCicloDTO dto) {
        CiclosModel ciclo = cicloAtivoObrigatorio();
        if (dto.prtCod() != null) {
            exigirPraticaNaVigente(dto.prtCod());
        }
        IndicadorModel ind = mapper.toModel(dto);
        ind.setOrigem(EOrigemIndicador.COMPLEMENTAR);
        ind.setCicCod(ciclo.getCicCod());
        ind.setSituacao(EAtivoInativo.ATIVO);
        indicadores.save(ind);
        return comLabels(List.of(ind)).get(0);
    }

    /** Práticas ATIVAS da metodologia vigente (para o seletor de vínculo do complementar). */
    @Transactional(readOnly = true)
    public List<PraticaOpcaoDTO> vinculos() {
        VersaoMetodologiaModel vigente = versoes
                .findFirstBySituacaoOrderByVerCodDesc(ESituacaoVersao.VIGENTE).orElse(null);
        if (vigente == null) {
            return List.of();
        }
        Map<Long, String> nomeProcesso = new HashMap<>();
        for (ProcessoModel proc : processos.findByVerCodOrderByOrdemAscPrcCodAsc(vigente.getVerCod())) {
            if (proc.getSituacao() == EAtivoInativo.ATIVO) {
                nomeProcesso.put(proc.getPrcCod(), proc.getNome());
            }
        }
        List<PraticaOpcaoDTO> opcoes = new ArrayList<>();
        for (PraticaModel pr : praticas.findByPrcCodIn(nomeProcesso.keySet())) {
            if (pr.getSituacao() != EAtivoInativo.ATIVO) {
                continue;
            }
            opcoes.add(new PraticaOpcaoDTO(
                    pr.getPrtCod(), pr.getNome(), pr.getPrcCod(), nomeProcesso.get(pr.getPrcCod())));
        }
        return opcoes;
    }

    // ---- apoio ----

    private Set<Long> praticasAtivasDaVersao(Long verCod) {
        List<Long> prcCods = processos.findByVerCodOrderByOrdemAscPrcCodAsc(verCod).stream()
                .filter(p -> p.getSituacao() == EAtivoInativo.ATIVO)
                .map(ProcessoModel::getPrcCod)
                .toList();
        if (prcCods.isEmpty()) {
            return Set.of();
        }
        return praticas.findByPrcCodIn(prcCods).stream()
                .filter(pr -> pr.getSituacao() == EAtivoInativo.ATIVO)
                .map(PraticaModel::getPrtCod)
                .collect(Collectors.toSet());
    }

    /** Resolve os rótulos do "Vínculo CERNE" (processo/prática) em lote e monta os DTOs. */
    private List<IndicadorCicloDTO> comLabels(List<IndicadorModel> lista) {
        Set<Long> prtCods = lista.stream()
                .map(IndicadorModel::getPrtCod).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, PraticaModel> praticaPorId = new HashMap<>();
        for (PraticaModel pr : praticas.findAllById(prtCods)) {
            praticaPorId.put(pr.getPrtCod(), pr);
        }
        Set<Long> prcCods = praticaPorId.values().stream()
                .map(PraticaModel::getPrcCod).collect(Collectors.toSet());
        Map<Long, String> nomeProcesso = new HashMap<>();
        for (ProcessoModel proc : processos.findAllById(prcCods)) {
            nomeProcesso.put(proc.getPrcCod(), proc.getNome());
        }
        List<IndicadorCicloDTO> out = new ArrayList<>();
        for (IndicadorModel ind : lista) {
            PraticaModel pratica = ind.getPrtCod() == null ? null : praticaPorId.get(ind.getPrtCod());
            String praticaNome = pratica == null ? null : pratica.getNome();
            String processoNome = pratica == null ? null : nomeProcesso.get(pratica.getPrcCod());
            out.add(mapper.toDTO(ind, processoNome, praticaNome));
        }
        return out;
    }

    /** Garante que a prática pertence à metodologia vigente (prática → processo → versão VIGENTE). */
    private void exigirPraticaNaVigente(Long prtCod) {
        VersaoMetodologiaModel vigente = versoes
                .findFirstBySituacaoOrderByVerCodDesc(ESituacaoVersao.VIGENTE)
                .orElseThrow(() -> new RegraNegocioException("Não há metodologia vigente."));
        PraticaModel pratica = praticas.findById(prtCod)
                .orElseThrow(() -> new NaoEncontradoException("Prática", prtCod));
        ProcessoModel processo = processos.findById(pratica.getPrcCod())
                .orElseThrow(() -> new NaoEncontradoException("Processo", pratica.getPrcCod()));
        if (!processo.getVerCod().equals(vigente.getVerCod())) {
            throw new RegraNegocioException("A prática não pertence à metodologia vigente.");
        }
    }

    private CiclosModel cicloAtivo() {
        List<CiclosModel> ativos = ciclos.findByStatus(EStatusCiclo.ATIVO);
        return ativos.isEmpty() ? null : ativos.get(0);
    }

    private CiclosModel cicloAtivoObrigatorio() {
        CiclosModel ciclo = cicloAtivo();
        if (ciclo == null) {
            throw new RegraNegocioException("Não há ciclo ativo. Abra um ciclo antes de definir indicadores.");
        }
        return ciclo;
    }
}

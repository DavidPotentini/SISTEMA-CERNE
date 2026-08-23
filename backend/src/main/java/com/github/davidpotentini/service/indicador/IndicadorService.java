package com.github.davidpotentini.service.indicador;

import com.github.davidpotentini.comum.erro.NaoEncontradoException;
import com.github.davidpotentini.comum.erro.RegraNegocioException;
import com.github.davidpotentini.dto.indicador.IndicadorCicloDTO;
import com.github.davidpotentini.dto.indicador.PraticaOpcaoDTO;
import com.github.davidpotentini.enums.EAtivoInativo;
import com.github.davidpotentini.enums.EOrigemIndicador;
import com.github.davidpotentini.enums.EStatusCiclo;
import com.github.davidpotentini.mapper.indicador.IndicadorMapper;
import com.github.davidpotentini.model.ciclos.CiclosModel;
import com.github.davidpotentini.model.contas.ContasModel;
import com.github.davidpotentini.model.indicador.IndicadorModel;
import com.github.davidpotentini.model.metodologia.IndicadorMetodologiaModel;
import com.github.davidpotentini.model.metodologia.PraticaModel;
import com.github.davidpotentini.model.metodologia.ProcessoModel;
import com.github.davidpotentini.model.pessoas.PessoasModel;
import com.github.davidpotentini.repository.ciclos.CiclosRepository;
import com.github.davidpotentini.repository.contas.ContasRepository;
import com.github.davidpotentini.repository.indicador.IndicadorRepository;
import com.github.davidpotentini.repository.metodologia.IndicadorMetodologiaRepository;
import com.github.davidpotentini.repository.metodologia.PraticaRepository;
import com.github.davidpotentini.repository.metodologia.ProcessoRepository;
import com.github.davidpotentini.repository.pessoas.PessoasRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Indicadores do ciclo ativo (schema do tenant vem do JWT). "Gerar indicadores do ciclo" copia os
 * indicadores ATIVOS da metodologia vigente (origem {@code METODOLOGIA_CERNE}), substituindo os
 * gerados anteriormente e preservando os {@code COMPLEMENTAR}. "Definir complementar" inclui um
 * indicador manual. O "Vínculo CERNE" (processo/prática) é resolvido a partir do {@code PRT_COD}.
 */
@Service
public class IndicadorService {

    private final IndicadorRepository indicadores;
    private final ProcessoRepository processos;
    private final PraticaRepository praticas;
    private final IndicadorMetodologiaRepository indicadoresMetodologia;
    private final CiclosRepository ciclos;
    private final PessoasRepository pessoas;
    private final ContasRepository contas;
    private final IndicadorMapper mapper;

    public IndicadorService(IndicadorRepository indicadores,
                            ProcessoRepository processos, PraticaRepository praticas,
                            IndicadorMetodologiaRepository indicadoresMetodologia,
                            CiclosRepository ciclos, PessoasRepository pessoas,
                            ContasRepository contas, IndicadorMapper mapper) {
        this.indicadores = indicadores;
        this.processos = processos;
        this.praticas = praticas;
        this.indicadoresMetodologia = indicadoresMetodologia;
        this.ciclos = ciclos;
        this.pessoas = pessoas;
        this.contas = contas;
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
     * Gera os indicadores do ciclo a partir da metodologia: copia os indicadores ATIVOS das práticas
     * ATIVAS, com origem {@code METODOLOGIA_CERNE}. Substitui os gerados anteriormente (os
     * complementares permanecem).
     */
    @Transactional(rollbackFor = Exception.class)
    public List<IndicadorCicloDTO> gerarDoCiclo() {
        CiclosModel ciclo = cicloAtivoObrigatorio();

        indicadores.deleteByCicCodAndOrigem(ciclo.getCicCod(), EOrigemIndicador.METODOLOGIA_CERNE);

        Set<Long> praticasAtivas = praticasAtivas();
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
            exigirPraticaExiste(dto.prtCod());
        }
        IndicadorModel ind = mapper.toModel(dto);
        ind.setOrigem(EOrigemIndicador.COMPLEMENTAR);
        ind.setCicCod(ciclo.getCicCod());
        ind.setSituacao(EAtivoInativo.ATIVO);
        indicadores.save(ind);
        return comLabels(List.of(ind)).get(0);
    }

    /**
     * Define o responsável pela apuração de um indicador do ciclo. É o único campo editável dos
     * indicadores gerados da metodologia; {@code respPesCod} nulo desvincula o responsável.
     */
    @Transactional(rollbackFor = Exception.class)
    public IndicadorCicloDTO definirResponsavel(Long indCod, Long respPesCod) {
        IndicadorModel ind = indicadores.findById(indCod)
                .orElseThrow(() -> new NaoEncontradoException("Indicador", indCod));
        ind.setRespPesCod(respPesCod);
        indicadores.save(ind);
        return comLabels(List.of(ind)).get(0);
    }

    /** Práticas ATIVAS da metodologia (para o seletor de vínculo do complementar). */
    @Transactional(readOnly = true)
    public List<PraticaOpcaoDTO> vinculos() {
        Map<Long, String> nomeProcesso = new HashMap<>();
        for (ProcessoModel proc : processos.findAllByOrderByOrdemAscPrcCodAsc()) {
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

    private Set<Long> praticasAtivas() {
        List<Long> prcCods = new ArrayList<>();
        for (ProcessoModel proc : processos.findAllByOrderByOrdemAscPrcCodAsc()) {
            if (proc.getSituacao() == EAtivoInativo.ATIVO) {
                prcCods.add(proc.getPrcCod());
            }
        }
        Set<Long> prtCods = new HashSet<>();
        if (prcCods.isEmpty()) {
            return prtCods;
        }
        for (PraticaModel pr : praticas.findByPrcCodIn(prcCods)) {
            if (pr.getSituacao() == EAtivoInativo.ATIVO) {
                prtCods.add(pr.getPrtCod());
            }
        }
        return prtCods;
    }

    /** Resolve os rótulos do "Vínculo CERNE" (processo/prática) e o responsável em lote e monta os DTOs. */
    private List<IndicadorCicloDTO> comLabels(List<IndicadorModel> lista) {
        Set<Long> prtCods = new HashSet<>();
        for (IndicadorModel ind : lista) {
            if (ind.getPrtCod() != null) {
                prtCods.add(ind.getPrtCod());
            }
        }
        Map<Long, PraticaModel> praticaPorId = new HashMap<>();
        Set<Long> prcCods = new HashSet<>();
        for (PraticaModel pr : praticas.findAllById(prtCods)) {
            praticaPorId.put(pr.getPrtCod(), pr);
            prcCods.add(pr.getPrcCod());
        }
        Map<Long, String> nomeProcesso = new HashMap<>();
        for (ProcessoModel proc : processos.findAllById(prcCods)) {
            nomeProcesso.put(proc.getPrcCod(), proc.getNome());
        }
        Map<Long, String> nomeResponsavel = new HashMap<>();
        List<IndicadorCicloDTO> out = new ArrayList<>();
        for (IndicadorModel ind : lista) {
            PraticaModel pratica = ind.getPrtCod() == null ? null : praticaPorId.get(ind.getPrtCod());
            String praticaNome = pratica == null ? null : pratica.getNome();
            String processoNome = pratica == null ? null : nomeProcesso.get(pratica.getPrcCod());
            out.add(mapper.toDTO(ind, processoNome, praticaNome,
                    rotuloResponsavel(ind.getRespPesCod(), nomeResponsavel)));
        }
        return out;
    }

    /** Nome do responsável (PESSOAS → public.CONTAS), memorizado por {@code PES_COD}; {@code null} se não resolvível. */
    private String rotuloResponsavel(Long pesCod, Map<Long, String> cache) {
        if (pesCod == null) {
            return null;
        }
        if (cache.containsKey(pesCod)) {
            return cache.get(pesCod);
        }
        String nome = null;
        PessoasModel pessoa = pessoas.findById(pesCod).orElse(null);
        if (pessoa != null) {
            ContasModel conta = contas.findById(pessoa.getCtaCod()).orElse(null);
            if (conta != null) {
                nome = conta.getNome();
            }
        }
        cache.put(pesCod, nome);
        return nome;
    }

    /** Garante que a prática existe na metodologia. */
    private void exigirPraticaExiste(Long prtCod) {
        if (!praticas.existsById(prtCod)) {
            throw new NaoEncontradoException("Prática", prtCod);
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

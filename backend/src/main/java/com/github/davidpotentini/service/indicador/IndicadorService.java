package com.github.davidpotentini.service.indicador;

import com.github.davidpotentini.comum.erro.NaoEncontradoException;
import com.github.davidpotentini.comum.erro.RegraNegocioException;
import com.github.davidpotentini.dto.indicador.IndicadorCicloDTO;
import com.github.davidpotentini.dto.indicador.PraticaOpcaoDTO;
import com.github.davidpotentini.enums.EAtivoInativo;
import com.github.davidpotentini.enums.EOrigemIndicador;
import com.github.davidpotentini.comum.ciclo.CicloContexto;
import com.github.davidpotentini.mapper.indicador.IndicadorMapper;
import com.github.davidpotentini.model.ciclos.CiclosModel;
import com.github.davidpotentini.model.contas.ContasModel;
import com.github.davidpotentini.model.estruturaciclo.PraticaCicloModel;
import com.github.davidpotentini.model.estruturaciclo.ProcessoCicloModel;
import com.github.davidpotentini.model.indicador.IndicadorModel;
import com.github.davidpotentini.model.metodologia.IndicadorMetodologiaModel;
import com.github.davidpotentini.model.metodologia.PraticaModel;
import com.github.davidpotentini.model.metodologia.ProcessoModel;
import com.github.davidpotentini.model.pessoas.PessoasModel;
import com.github.davidpotentini.repository.contas.ContasRepository;
import com.github.davidpotentini.repository.estruturaciclo.PraticaCicloRepository;
import com.github.davidpotentini.repository.estruturaciclo.ProcessoCicloRepository;
import com.github.davidpotentini.repository.indicador.IndicadorRepository;
import com.github.davidpotentini.repository.indicador.MetaRepository;
import com.github.davidpotentini.repository.metodologia.IndicadorMetodologiaRepository;
import com.github.davidpotentini.repository.metodologia.PraticaRepository;
import com.github.davidpotentini.repository.metodologia.ProcessoRepository;
import com.github.davidpotentini.repository.pessoas.PessoasRepository;
import com.github.davidpotentini.service.estruturaciclo.EstruturaCicloService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class IndicadorService {

    private final IndicadorRepository indicadores;
    private final MetaRepository metas;
    private final ProcessoRepository processos;
    private final PraticaRepository praticas;
    private final IndicadorMetodologiaRepository indicadoresMetodologia;
    private final ProcessoCicloRepository processosCiclo;
    private final PraticaCicloRepository praticasCiclo;
    private final EstruturaCicloService estruturaCiclo;
    private final CicloContexto cicloContexto;
    private final PessoasRepository pessoas;
    private final ContasRepository contas;
    private final IndicadorMapper mapper;

    public IndicadorService(IndicadorRepository indicadores, MetaRepository metas,
                            ProcessoRepository processos, PraticaRepository praticas,
                            IndicadorMetodologiaRepository indicadoresMetodologia,
                            ProcessoCicloRepository processosCiclo, PraticaCicloRepository praticasCiclo,
                            EstruturaCicloService estruturaCiclo,
                            CicloContexto cicloContexto, PessoasRepository pessoas,
                            ContasRepository contas, IndicadorMapper mapper) {
        this.indicadores = indicadores;
        this.metas = metas;
        this.processos = processos;
        this.praticas = praticas;
        this.indicadoresMetodologia = indicadoresMetodologia;
        this.processosCiclo = processosCiclo;
        this.praticasCiclo = praticasCiclo;
        this.estruturaCiclo = estruturaCiclo;
        this.cicloContexto = cicloContexto;
        this.pessoas = pessoas;
        this.contas = contas;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<IndicadorCicloDTO> listar() {
        CiclosModel ciclo = cicloContexto.emFoco();
        if (ciclo == null) {
            return List.of();
        }
        return comLabels(emOrdemEstrutural(
                ciclo.getCicCod(), indicadores.findByCicCodOrderByNomeAsc(ciclo.getCicCod())));
    }

    /** Reordena pela posição estrutural do ciclo (processo/prática por {@code ordem}); sem vínculo vai ao fim. */
    private List<IndicadorModel> emOrdemEstrutural(Long cicCod, List<IndicadorModel> porNome) {
        Map<Long, List<IndicadorModel>> porPratica = new HashMap<>();
        List<IndicadorModel> semVinculo = new ArrayList<>();
        for (IndicadorModel ind : porNome) {
            if (ind.getPrtcCod() == null) {
                semVinculo.add(ind);
            } else {
                porPratica.computeIfAbsent(ind.getPrtcCod(), k -> new ArrayList<>()).add(ind);
            }
        }
        List<IndicadorModel> ordenados = new ArrayList<>();
        for (ProcessoCicloModel proc : processosCiclo.findByCicCodOrderByOrdemAscPrccCodAsc(cicCod)) {
            for (PraticaCicloModel pr : praticasCiclo.findByPrccCodOrderByOrdemAscPrtcCodAsc(proc.getPrccCod())) {
                ordenados.addAll(porPratica.getOrDefault(pr.getPrtcCod(), List.of()));
            }
        }
        ordenados.addAll(semVinculo);
        return ordenados;
    }

    /** Copia os indicadores ATIVOS da metodologia; substitui os gerados anteriormente (complementares permanecem). */
    @Transactional(rollbackFor = Exception.class)
    public List<IndicadorCicloDTO> gerarDoCiclo() {
        CiclosModel ciclo = cicloEmFocoObrigatorio();

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
                ind.setPrtcCod(estruturaCiclo.garantirPratica(ciclo.getCicCod(), base.getPrtCod()));
                ind.setCicCod(ciclo.getCicCod());
                ind.setUnidade(base.getUnidade());
                ind.setPeriodicidade(base.getPeriodicidade());
                ind.setSituacao(EAtivoInativo.ATIVO);
                indicadores.save(ind);
            }
        }
        return comLabels(indicadores.findByCicCodOrderByNomeAsc(ciclo.getCicCod()));
    }

    /** Trava do "Gerar do ciclo": conta como edição um complementar, um responsável definido, ou qualquer meta. */
    @Transactional(readOnly = true)
    public boolean cicloTemEdicoes(Long cicCod) {
        return indicadores.existsByCicCodAndOrigem(cicCod, EOrigemIndicador.COMPLEMENTAR)
                || indicadores.existsByCicCodAndRespPesCodNotNull(cicCod)
                || metas.existsByCiclo(cicCod);
    }

    @Transactional(rollbackFor = Exception.class)
    public IndicadorCicloDTO definirComplementar(IndicadorCicloDTO dto) {
        CiclosModel ciclo = cicloEmFocoObrigatorio();
        if (dto.prtcCod() != null) {
            exigirPraticaCicloExiste(ciclo.getCicCod(), dto.prtcCod());
        }
        IndicadorModel ind = mapper.toModel(dto);
        ind.setOrigem(EOrigemIndicador.COMPLEMENTAR);
        ind.setCicCod(ciclo.getCicCod());
        ind.setSituacao(EAtivoInativo.ATIVO);
        indicadores.save(ind);
        return comLabels(List.of(ind)).get(0);
    }

    /** {@code respPesCod} nulo desvincula o responsável. */
    @Transactional(rollbackFor = Exception.class)
    public IndicadorCicloDTO definirResponsavel(Long indCod, Long respPesCod) {
        IndicadorModel ind = indicadores.findById(indCod)
                .orElseThrow(() -> new NaoEncontradoException("Indicador", indCod));
        ind.setRespPesCod(respPesCod);
        indicadores.save(ind);
        return comLabels(List.of(ind)).get(0);
    }

    @Transactional(readOnly = true)
    public List<PraticaOpcaoDTO> vinculos() {
        CiclosModel ciclo = cicloContexto.emFoco();
        if (ciclo == null) {
            return List.of();
        }
        List<PraticaOpcaoDTO> opcoes = new ArrayList<>();
        for (ProcessoCicloModel proc : processosCiclo.findByCicCodOrderByOrdemAscPrccCodAsc(ciclo.getCicCod())) {
            for (PraticaCicloModel pr : praticasCiclo.findByPrccCodOrderByOrdemAscPrtcCodAsc(proc.getPrccCod())) {
                opcoes.add(new PraticaOpcaoDTO(
                        pr.getPrtcCod(), pr.getNome(), pr.getPrccCod(), proc.getNome()));
            }
        }
        return opcoes;
    }

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

    private List<IndicadorCicloDTO> comLabels(List<IndicadorModel> lista) {
        Set<Long> prtcCods = new HashSet<>();
        for (IndicadorModel ind : lista) {
            if (ind.getPrtcCod() != null) {
                prtcCods.add(ind.getPrtcCod());
            }
        }
        Map<Long, PraticaCicloModel> praticaPorId = new HashMap<>();
        Set<Long> prccCods = new HashSet<>();
        for (PraticaCicloModel pr : praticasCiclo.findAllById(prtcCods)) {
            praticaPorId.put(pr.getPrtcCod(), pr);
            prccCods.add(pr.getPrccCod());
        }
        Map<Long, String> nomeProcesso = new HashMap<>();
        for (ProcessoCicloModel proc : processosCiclo.findAllById(prccCods)) {
            nomeProcesso.put(proc.getPrccCod(), proc.getNome());
        }
        Map<Long, String> nomeResponsavel = new HashMap<>();
        List<IndicadorCicloDTO> out = new ArrayList<>();
        for (IndicadorModel ind : lista) {
            PraticaCicloModel pratica = ind.getPrtcCod() == null ? null : praticaPorId.get(ind.getPrtcCod());
            String praticaNome = pratica == null ? null : pratica.getNome();
            String processoNome = pratica == null ? null : nomeProcesso.get(pratica.getPrccCod());
            out.add(mapper.toDTO(ind, processoNome, praticaNome,
                    rotuloResponsavel(ind.getRespPesCod(), nomeResponsavel)));
        }
        return out;
    }

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

    private void exigirPraticaCicloExiste(Long cicCod, Long prtcCod) {
        PraticaCicloModel pratica = praticasCiclo.findById(prtcCod).orElse(null);
        if (pratica == null || !pratica.getCicCod().equals(cicCod)) {
            throw new NaoEncontradoException("Prática", prtcCod);
        }
    }

    private CiclosModel cicloEmFocoObrigatorio() {
        CiclosModel ciclo = cicloContexto.emFoco();
        if (ciclo == null) {
            throw new RegraNegocioException("Não há ciclo ativo. Abra um ciclo antes de definir indicadores.");
        }
        return ciclo;
    }
}

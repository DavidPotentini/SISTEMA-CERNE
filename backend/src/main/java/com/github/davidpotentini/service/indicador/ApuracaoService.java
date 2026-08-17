package com.github.davidpotentini.service.indicador;

import com.github.davidpotentini.comum.erro.NaoEncontradoException;
import com.github.davidpotentini.comum.erro.RegraNegocioException;
import com.github.davidpotentini.comum.tenant.SessaoContext;
import com.github.davidpotentini.dto.indicador.ApuracaoIndicadorDTO;
import com.github.davidpotentini.dto.indicador.IndicadorCicloDTO;
import com.github.davidpotentini.dto.indicador.PeriodoApuracaoDTO;
import com.github.davidpotentini.model.contas.ContasModel;
import com.github.davidpotentini.model.indicador.MetaModel;
import com.github.davidpotentini.model.indicador.ResultadoModel;
import com.github.davidpotentini.model.pessoas.PessoasModel;
import com.github.davidpotentini.repository.contas.ContasRepository;
import com.github.davidpotentini.repository.indicador.IndicadorRepository;
import com.github.davidpotentini.repository.indicador.MetaRepository;
import com.github.davidpotentini.repository.indicador.ResultadoRepository;
import com.github.davidpotentini.repository.pessoas.PessoasRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Apuração de indicadores do ciclo ativo (schema do tenant vem do JWT). A listagem reaproveita os
 * indicadores do ciclo ({@link IndicadorService}) e acrescenta o resumo de apuração
 * (apurados/total de períodos). Registrar um resultado carimba o usuário logado e a data.
 */
@Service
public class ApuracaoService {

    private final IndicadorService indicadorService;
    private final IndicadorRepository indicadores;
    private final MetaRepository metas;
    private final ResultadoRepository resultados;
    private final PessoasRepository pessoas;
    private final ContasRepository contas;

    public ApuracaoService(IndicadorService indicadorService, IndicadorRepository indicadores,
                           MetaRepository metas, ResultadoRepository resultados,
                           PessoasRepository pessoas, ContasRepository contas) {
        this.indicadorService = indicadorService;
        this.indicadores = indicadores;
        this.metas = metas;
        this.resultados = resultados;
        this.pessoas = pessoas;
        this.contas = contas;
    }

    /** Indicadores do ciclo ativo com o resumo de apuração (apurados/total de períodos). */
    @Transactional(readOnly = true)
    public List<ApuracaoIndicadorDTO> listar() {
        List<IndicadorCicloDTO> base = indicadorService.listar();
        if (base.isEmpty()) {
            return List.of();
        }
        List<Long> indCods = new ArrayList<>();
        for (IndicadorCicloDTO ind : base) {
            indCods.add(ind.indCod());
        }

        // metCods por indicador + conjunto de metCods apurados (em lote)
        Map<Long, List<Long>> metCodsPorInd = new HashMap<>();
        List<Long> todosMetCods = new ArrayList<>();
        for (MetaModel m : metas.findByIndCodIn(indCods)) {
            metCodsPorInd.computeIfAbsent(m.getIndCod(), k -> new ArrayList<>()).add(m.getMetCod());
            todosMetCods.add(m.getMetCod());
        }
        Set<Long> apuradosMetCods = new HashSet<>();
        for (ResultadoModel r : resultados.findByMetCodIn(todosMetCods)) {
            apuradosMetCods.add(r.getMetCod());
        }

        List<ApuracaoIndicadorDTO> lista = new ArrayList<>();
        for (IndicadorCicloDTO ind : base) {
            List<Long> metCods = metCodsPorInd.getOrDefault(ind.indCod(), List.of());
            int apurados = 0;
            for (Long metCod : metCods) {
                if (apuradosMetCods.contains(metCod)) {
                    apurados++;
                }
            }
            lista.add(new ApuracaoIndicadorDTO(
                    ind.indCod(), ind.nome(), ind.processoNome(), ind.praticaNome(),
                    ind.periodicidade(), ind.unidade(), metCods.size(), apurados));
        }
        return lista;
    }

    /** Períodos de um indicador com o resultado apurado (quando houver). */
    @Transactional(readOnly = true)
    public List<PeriodoApuracaoDTO> periodos(Long indCod) {
        exigirIndicador(indCod);
        List<MetaModel> periodos = metas.findByIndCodOrderByDataInicioApuracaoAscMetCodAsc(indCod);
        List<Long> metCods = new ArrayList<>();
        for (MetaModel m : periodos) {
            metCods.add(m.getMetCod());
        }
        Map<Long, ResultadoModel> resPorMet = new HashMap<>();
        for (ResultadoModel r : resultados.findByMetCodIn(metCods)) {
            resPorMet.put(r.getMetCod(), r);
        }

        List<PeriodoApuracaoDTO> lista = new ArrayList<>();
        for (MetaModel m : periodos) {
            lista.add(toDTO(m, resPorMet.get(m.getMetCod())));
        }
        return lista;
    }

    /** Registra (ou atualiza) o resultado de um período; carimba usuário logado e data. */
    @Transactional(rollbackFor = Exception.class)
    public PeriodoApuracaoDTO registrar(Long indCod, Long metCod, BigDecimal valor) {
        MetaModel meta = metas.findById(metCod)
                .orElseThrow(() -> new NaoEncontradoException("Meta", metCod));
        if (!meta.getIndCod().equals(indCod)) {
            throw new RegraNegocioException("A meta não pertence a este indicador.");
        }
        ResultadoModel resultado = resultados.findById(metCod).orElseGet(() -> {
            ResultadoModel novo = new ResultadoModel();
            novo.setMetCod(metCod);
            return novo;
        });
        resultado.setValor(valor);
        resultado.setRegPesCod(pessoaAtual());
        resultado.setDataRegistro(LocalDateTime.now());
        resultados.save(resultado);
        return toDTO(meta, resultado);
    }

    // ---- apoio ----

    private PeriodoApuracaoDTO toDTO(MetaModel meta, ResultadoModel resultado) {
        return new PeriodoApuracaoDTO(
                meta.getMetCod(), meta.getValor(),
                meta.getDataInicioApuracao(), meta.getDataFimApuracao(),
                resultado == null ? null : resultado.getValor(),
                resultado == null ? null : rotuloResponsavel(resultado.getRegPesCod()),
                resultado == null ? null : resultado.getDataRegistro());
    }

    private void exigirIndicador(Long indCod) {
        if (!indicadores.existsById(indCod)) {
            throw new NaoEncontradoException("Indicador", indCod);
        }
    }

    /** Pessoa ({@code PES_COD}) do usuário logado neste tenant, ou {@code null} se não resolvível. */
    private Long pessoaAtual() {
        Long ctaCod = SessaoContext.contaAtual();
        if (ctaCod == null) {
            return null;
        }
        return pessoas.findByCtaCod(ctaCod).map(PessoasModel::getPesCod).orElse(null);
    }

    /** Nome de quem registrou (PESSOAS → public.CONTAS); {@code null} se não definido ou não resolvível. */
    private String rotuloResponsavel(Long pesCod) {
        if (pesCod == null) {
            return null;
        }
        PessoasModel pessoa = pessoas.findById(pesCod).orElse(null);
        if (pessoa == null) {
            return null;
        }
        return contas.findById(pessoa.getCtaCod()).map(ContasModel::getNome).orElse(null);
    }
}

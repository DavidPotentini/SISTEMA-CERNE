package com.github.davidpotentini.service.monitoramento;

import com.github.davidpotentini.comum.erro.NaoEncontradoException;
import com.github.davidpotentini.comum.erro.RegraNegocioException;
import com.github.davidpotentini.dto.monitoramento.AplicacaoDTO;
import com.github.davidpotentini.dto.monitoramento.EvolucaoRodadaDTO;
import com.github.davidpotentini.dto.monitoramento.PontuacaoDTO;
import com.github.davidpotentini.dto.monitoramento.RodadaDTO;
import com.github.davidpotentini.enums.EStatusCiclo;
import com.github.davidpotentini.enums.EStatusMonitoramento;
import com.github.davidpotentini.enums.ESituacaoRodada;
import com.github.davidpotentini.mapper.monitoramento.MonitoramentoMapper;
import com.github.davidpotentini.model.ciclos.CiclosModel;
import com.github.davidpotentini.model.contas.ContasModel;
import com.github.davidpotentini.model.empreendimentos.EmpreendimentosModel;
import com.github.davidpotentini.model.monitoramento.AvaliacaoModel;
import com.github.davidpotentini.model.monitoramento.PontuacaoId;
import com.github.davidpotentini.model.monitoramento.PontuacaoModel;
import com.github.davidpotentini.model.monitoramento.RodadaIncubadaId;
import com.github.davidpotentini.model.monitoramento.RodadaIncubadaModel;
import com.github.davidpotentini.model.monitoramento.RodadaModel;
import com.github.davidpotentini.model.pessoas.PessoasModel;
import com.github.davidpotentini.repository.ciclos.CiclosRepository;
import com.github.davidpotentini.repository.contas.ContasRepository;
import com.github.davidpotentini.repository.empreendimentos.EmpreendimentosRepository;
import com.github.davidpotentini.repository.monitoramento.AvaliacaoRepository;
import com.github.davidpotentini.repository.monitoramento.PontuacaoRepository;
import com.github.davidpotentini.repository.monitoramento.RodadaIncubadaRepository;
import com.github.davidpotentini.repository.monitoramento.RodadaRepository;
import com.github.davidpotentini.repository.pessoas.PessoasRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * Monitoramento das incubadas (schema do tenant vem do JWT). Uma rodada aplica a avaliação por eixos
 * CERNE a um conjunto de empreendimentos; a avaliação de cada um é criada ao revisar. A listagem de
 * rodadas alimenta a aba "Rodadas"; as aplicações (cards com pontuações, recomendação e status)
 * alimentam a aba "Aplicações e pontuação".
 */
@Service
public class MonitoramentoService {

    private static final int NOTA_MIN = 0;
    private static final int NOTA_MAX = 5;

    private final RodadaRepository rodadas;
    private final RodadaIncubadaRepository incubadas;
    private final AvaliacaoRepository avaliacoes;
    private final PontuacaoRepository pontuacoes;
    private final EmpreendimentosRepository empreendimentos;
    private final CiclosRepository ciclos;
    private final PessoasRepository pessoas;
    private final ContasRepository contas;
    private final MonitoramentoMapper mapper;

    public MonitoramentoService(RodadaRepository rodadas, RodadaIncubadaRepository incubadas,
                                AvaliacaoRepository avaliacoes, PontuacaoRepository pontuacoes,
                                EmpreendimentosRepository empreendimentos, CiclosRepository ciclos,
                                PessoasRepository pessoas, ContasRepository contas,
                                MonitoramentoMapper mapper) {
        this.rodadas = rodadas;
        this.incubadas = incubadas;
        this.avaliacoes = avaliacoes;
        this.pontuacoes = pontuacoes;
        this.empreendimentos = empreendimentos;
        this.ciclos = ciclos;
        this.pessoas = pessoas;
        this.contas = contas;
        this.mapper = mapper;
    }

    // ---- rodadas ----

    @Transactional(readOnly = true)
    public List<RodadaDTO> listarRodadas() {
        List<RodadaDTO> lista = new ArrayList<>();
        for (RodadaModel rodada : rodadas.findAllByOrderByRodCodDesc()) {
            lista.add(mapper.toDTO(rodada, nomeResponsavel(rodada.getRespPesCod())));
        }
        return lista;
    }

    /** Planeja uma rodada no ciclo ativo e registra os empreendimentos participantes. */
    @Transactional(rollbackFor = Exception.class)
    public RodadaDTO planejar(RodadaDTO dto) {
        RodadaModel rodada = mapper.toModel(dto);
        rodada.setCicCod(cicloAtivo());
        rodada.setSituacao(ESituacaoRodada.EM_ANDAMENTO);
        rodadas.save(rodada);

        if (dto.empCods() != null) {
            for (Long empCod : new LinkedHashSet<>(dto.empCods())) {
                if (!empreendimentos.existsById(empCod)) {
                    throw new NaoEncontradoException("Empreendimento", empCod);
                }
                RodadaIncubadaModel participacao = new RodadaIncubadaModel();
                participacao.setRodCod(rodada.getRodCod());
                participacao.setEmpCod(empCod);
                incubadas.save(participacao);
            }
        }
        return mapper.toDTO(rodada, nomeResponsavel(rodada.getRespPesCod()));
    }

    /** Encerra a rodada (situação {@code CONCLUIDA}). */
    @Transactional(rollbackFor = Exception.class)
    public RodadaDTO concluir(Long rodCod) {
        RodadaModel rodada = exigirRodada(rodCod);
        rodada.setSituacao(ESituacaoRodada.CONCLUIDA);
        rodadas.save(rodada);
        return mapper.toDTO(rodada, nomeResponsavel(rodada.getRespPesCod()));
    }

    // ---- aplicações e pontuação ----

    /** Cards da rodada: um por empreendimento participante, com a avaliação (quando revisado). */
    @Transactional(readOnly = true)
    public List<AplicacaoDTO> aplicacoes(Long rodCod) {
        exigirRodada(rodCod);

        List<Long> empCods = new ArrayList<>();
        for (RodadaIncubadaModel p : incubadas.findByRodCod(rodCod)) {
            empCods.add(p.getEmpCod());
        }
        if (empCods.isEmpty()) {
            return List.of();
        }

        Map<Long, String> nomePorEmp = new HashMap<>();
        for (EmpreendimentosModel emp : empreendimentos.findAllById(empCods)) {
            nomePorEmp.put(emp.getEmpCod(), emp.getNome());
        }

        Map<Long, AvaliacaoModel> avaliacaoPorEmp = new HashMap<>();
        List<Long> avaCods = new ArrayList<>();
        for (AvaliacaoModel av : avaliacoes.findByRodCod(rodCod)) {
            avaliacaoPorEmp.put(av.getEmpCod(), av);
            avaCods.add(av.getAvaCod());
        }
        Map<Long, List<PontuacaoModel>> pontuacoesPorAva = new HashMap<>();
        for (PontuacaoModel pt : pontuacoes.findByAvaCodIn(avaCods)) {
            pontuacoesPorAva.computeIfAbsent(pt.getAvaCod(), k -> new ArrayList<>()).add(pt);
        }

        List<AplicacaoDTO> cards = new ArrayList<>();
        for (Long empCod : empCods) {
            AvaliacaoModel av = avaliacaoPorEmp.get(empCod);
            List<PontuacaoModel> pts = av == null ? List.of()
                    : pontuacoesPorAva.getOrDefault(av.getAvaCod(), List.of());
            cards.add(montarAplicacao(empCod, nomePorEmp.get(empCod), av, pts));
        }
        cards.sort(Comparator.comparing(a -> a.empNome() == null ? "" : a.empNome().toLowerCase()));
        return cards;
    }

    /** Revisa a incubada: define pontuações por eixo, recomendação, observação e status. */
    @Transactional(rollbackFor = Exception.class)
    public AplicacaoDTO revisar(Long rodCod, Long empCod, AplicacaoDTO dto) {
        exigirRodada(rodCod);
        if (!incubadas.existsById(new RodadaIncubadaId(rodCod, empCod))) {
            throw new RegraNegocioException("O empreendimento não faz parte desta rodada.");
        }

        AvaliacaoModel avaliacao = avaliacoes.findByRodCodAndEmpCod(rodCod, empCod).orElseGet(() -> {
            AvaliacaoModel nova = new AvaliacaoModel();
            nova.setRodCod(rodCod);
            nova.setEmpCod(empCod);
            return nova;
        });
        avaliacao.setStatus(dto.status() != null ? dto.status() : EStatusMonitoramento.EM_ANDAMENTO);
        avaliacao.setRecomendacao(dto.recomendacao());
        avaliacao.setObservacao(dto.observacao());
        avaliacao.setData(LocalDate.now());
        avaliacoes.save(avaliacao);

        // Substitui no lugar a nota de cada eixo informado (upsert por dimensão).
        List<PontuacaoModel> pts = new ArrayList<>();
        if (dto.pontuacoes() != null) {
            for (PontuacaoDTO p : dto.pontuacoes()) {
                if (p.dimensao() == null || p.pontuacao() == null) {
                    continue;
                }
                validarNota(p.pontuacao());
                PontuacaoModel pt = pontuacoes.findById(new PontuacaoId(avaliacao.getAvaCod(), p.dimensao()))
                        .orElseGet(() -> {
                            PontuacaoModel novo = new PontuacaoModel();
                            novo.setAvaCod(avaliacao.getAvaCod());
                            novo.setDimensao(p.dimensao());
                            return novo;
                        });
                pt.setPontuacao(p.pontuacao().shortValue());
                pontuacoes.save(pt);
                pts.add(pt);
            }
        }

        String empNome = empreendimentos.findById(empCod).map(EmpreendimentosModel::getNome).orElse(null);
        return montarAplicacao(empCod, empNome, avaliacao, pts);
    }

    // ---- radar de evolução ----

    /**
     * Série do radar de evolução de um empreendimento: uma entrada por rodada em que ele foi avaliado,
     * com as notas por eixo ({@code pontuacoes}, cada uma com {@code dimensao}), em ordem cronológica
     * (rodada mais antiga primeiro). Sem avaliações → lista vazia.
     */
    @Transactional(readOnly = true)
    public List<EvolucaoRodadaDTO> evolucao(Long empCod) {
        if (!empreendimentos.existsById(empCod)) {
            throw new NaoEncontradoException("Empreendimento", empCod);
        }
        List<AvaliacaoModel> avs = new ArrayList<>(avaliacoes.findByEmpCod(empCod));
        if (avs.isEmpty()) {
            return List.of();
        }

        List<Long> avaCods = new ArrayList<>();
        List<Long> rodCods = new ArrayList<>();
        for (AvaliacaoModel av : avs) {
            avaCods.add(av.getAvaCod());
            rodCods.add(av.getRodCod());
        }

        Map<Long, String> nomeRodada = new HashMap<>();
        for (RodadaModel r : rodadas.findAllById(rodCods)) {
            nomeRodada.put(r.getRodCod(), r.getNome());
        }
        Map<Long, List<PontuacaoModel>> pontuacoesPorAva = new HashMap<>();
        for (PontuacaoModel pt : pontuacoes.findByAvaCodIn(avaCods)) {
            pontuacoesPorAva.computeIfAbsent(pt.getAvaCod(), k -> new ArrayList<>()).add(pt);
        }

        avs.sort(Comparator
                .comparing(AvaliacaoModel::getData, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(AvaliacaoModel::getRodCod));

        List<EvolucaoRodadaDTO> serie = new ArrayList<>();
        for (AvaliacaoModel av : avs) {
            List<PontuacaoModel> pts = pontuacoesPorAva.getOrDefault(av.getAvaCod(), List.of());
            serie.add(new EvolucaoRodadaDTO(
                    av.getRodCod(),
                    nomeRodada.get(av.getRodCod()),
                    av.getData(),
                    mapper.toDTOList(pts)));
        }
        return serie;
    }

    // ---- apoio ----

    private AplicacaoDTO montarAplicacao(Long empCod, String empNome, AvaliacaoModel av,
                                         List<PontuacaoModel> pts) {
        return new AplicacaoDTO(
                av == null ? null : av.getAvaCod(),
                empCod,
                empNome,
                av == null ? null : av.getStatus(),
                av == null ? null : av.getData(),
                av == null ? null : av.getRecomendacao(),
                av == null ? null : av.getObservacao(),
                mapper.toDTOList(pts));
    }

    private void validarNota(Integer nota) {
        if (nota < NOTA_MIN || nota > NOTA_MAX) {
            throw new RegraNegocioException("A pontuação de cada eixo deve estar entre 0 e 5.");
        }
    }

    private RodadaModel exigirRodada(Long rodCod) {
        return rodadas.findById(rodCod)
                .orElseThrow(() -> new NaoEncontradoException("Rodada", rodCod));
    }

    /** Ciclo ativo da incubadora, ou {@code null} se não houver. */
    private Long cicloAtivo() {
        List<CiclosModel> ativos = ciclos.findByStatus(EStatusCiclo.ATIVO);
        return ativos.isEmpty() ? null : ativos.get(0).getCicCod();
    }

    /** Nome do responsável (equipe → conta), ou {@code null} se não definido/inexistente. */
    private String nomeResponsavel(Long respPesCod) {
        if (respPesCod == null) {
            return null;
        }
        PessoasModel pessoa = pessoas.findById(respPesCod).orElse(null);
        if (pessoa == null) {
            return null;
        }
        return contas.findById(pessoa.getCtaCod()).map(ContasModel::getNome).orElse(null);
    }
}

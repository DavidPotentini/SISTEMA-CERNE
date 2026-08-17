package com.github.davidpotentini.service.metodologia;

import com.github.davidpotentini.comum.erro.NaoEncontradoException;
import com.github.davidpotentini.comum.erro.RegraNegocioException;
import com.github.davidpotentini.comum.tenant.SessaoContext;
import com.github.davidpotentini.dto.metodologia.IndicadorDTO;
import com.github.davidpotentini.dto.metodologia.PraticaDTO;
import com.github.davidpotentini.dto.metodologia.ProcessoDTO;
import com.github.davidpotentini.dto.metodologia.VersaoDTO;
import com.github.davidpotentini.enums.EAtivoInativo;
import com.github.davidpotentini.enums.ESituacaoVersao;
import com.github.davidpotentini.mapper.metodologia.MetodologiaMapper;
import com.github.davidpotentini.model.contas.ContasModel;
import com.github.davidpotentini.model.metodologia.IndicadorMetodologiaModel;
import com.github.davidpotentini.model.metodologia.PraticaModel;
import com.github.davidpotentini.model.metodologia.ProcessoModel;
import com.github.davidpotentini.model.metodologia.VersaoMetodologiaModel;
import com.github.davidpotentini.model.pessoas.PessoasModel;
import com.github.davidpotentini.repository.contas.ContasRepository;
import com.github.davidpotentini.repository.metodologia.IndicadorMetodologiaRepository;
import com.github.davidpotentini.repository.metodologia.PraticaRepository;
import com.github.davidpotentini.repository.metodologia.ProcessoRepository;
import com.github.davidpotentini.repository.metodologia.VersaoMetodologiaRepository;
import com.github.davidpotentini.repository.pessoas.PessoasRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Metodologia CERNE da incubadora logada. Roda no schema do próprio tenant (o JWT já deixou o
 * {@code TenantContext} ativo), então as tabelas são lidas/gravadas direto.
 *
 * <p>Versionamento (copy-on-publish): as abas editam sempre o RASCUNHO — a única versão de trabalho.
 * Qualquer mutação liga o flag {@code alterada}. Publicar clona a árvore
 * ({@code versão → processos → práticas → indicadores}) numa nova VIGENTE imutável, rebaixa a VIGENTE
 * anterior para HISTORICA e zera {@code alterada}. A criação de modelos consome a última VIGENTE.
 */
@Service
public class MetodologiaService {

    private final VersaoMetodologiaRepository versoes;
    private final ProcessoRepository processos;
    private final PraticaRepository praticas;
    private final IndicadorMetodologiaRepository indicadores;
    private final PessoasRepository pessoas;
    private final ContasRepository contas;
    private final MetodologiaMapper mapper;

    public MetodologiaService(VersaoMetodologiaRepository versoes, ProcessoRepository processos,
                              PraticaRepository praticas, IndicadorMetodologiaRepository indicadores,
                              PessoasRepository pessoas, ContasRepository contas,
                              MetodologiaMapper mapper) {
        this.versoes = versoes;
        this.processos = processos;
        this.praticas = praticas;
        this.indicadores = indicadores;
        this.pessoas = pessoas;
        this.contas = contas;
        this.mapper = mapper;
    }

    // ---- versão / publicação ----

    /** Versão de trabalho (RASCUNHO) — a que as abas editam; criada na primeira vez. */
    @Transactional(rollbackFor = Exception.class)
    public VersaoDTO versaoDeTrabalho() {
        return mapper.toDTO(obterOuCriarRascunho(), null);
    }

    /** Histórico de publicações (VIGENTE + HISTORICA), mais recentes primeiro. */
    @Transactional(readOnly = true)
    public List<VersaoDTO> listarVersoes() {
        List<VersaoDTO> lista = new ArrayList<>();
        for (VersaoMetodologiaModel v :
                versoes.findBySituacaoInOrderByVerCodDesc(List.of(ESituacaoVersao.VIGENTE, ESituacaoVersao.HISTORICA))) {
            lista.add(mapper.toDTO(v, nomePessoa(v.getPubPesCod())));
        }
        return lista;
    }

    /**
     * Publica o rascunho: clona a árvore numa nova VIGENTE, rebaixa a VIGENTE anterior para HISTORICA
     * e zera {@code alterada}. Só publica se houver alterações pendentes.
     */
    @Transactional(rollbackFor = Exception.class)
    public VersaoDTO publicar() {
        VersaoMetodologiaModel rascunho = obterOuCriarRascunho();
        if (!rascunho.isAlterada()) {
            throw new RegraNegocioException("Não há alterações para publicar.");
        }
        Long pubPesCod = pessoaAtual();
        String rotulo = "v" + (versoes.countBySituacaoNot(ESituacaoVersao.RASCUNHO) + 1);

        versoes.findFirstBySituacaoOrderByVerCodDesc(ESituacaoVersao.VIGENTE).ifPresent(vigente -> {
            vigente.setSituacao(ESituacaoVersao.HISTORICA);
            versoes.saveAndFlush(vigente);
        });

        VersaoMetodologiaModel publicada = clonarComoVigente(rascunho, rotulo, pubPesCod);
        rascunho.setAlterada(false);
        versoes.save(rascunho);
        return mapper.toDTO(publicada, nomePessoa(pubPesCod));
    }

    // ---- processos ----

    /** Processos da versão (accordions ordenados por {@code ordem}), cada um com suas práticas. */
    @Transactional(readOnly = true)
    public List<ProcessoDTO> listarProcessos(Long verCod) {
        List<ProcessoDTO> lista = new ArrayList<>();
        for (ProcessoModel processo : processos.findByVerCodOrderByOrdemAscPrcCodAsc(verCod)) {
            lista.add(montarProcesso(processo));
        }
        return lista;
    }

    /** Novo processo na versão informada; {@code ordem} é única — 409 se já usada. */
    @Transactional(rollbackFor = Exception.class)
    public ProcessoDTO criarProcesso(ProcessoDTO dto) {
        exigirVersao(dto.verCod());
        if (processos.existsByVerCodAndOrdem(dto.verCod(), dto.ordem())) {
            throw new RegraNegocioException("Já existe um processo com a ordem " + dto.ordem() + ".");
        }
        ProcessoModel processo = mapper.toModel(dto);
        processo.setSituacao(EAtivoInativo.ATIVO);
        processos.save(processo);
        marcarRascunhoAlterado();
        return mapper.toDTO(processo, List.of());
    }

    /** Edita ordem/nome/descrição do processo; a {@code ordem} segue única na versão (409 se colidir). */
    @Transactional(rollbackFor = Exception.class)
    public ProcessoDTO editarProcesso(Long prcCod, ProcessoDTO dto) {
        ProcessoModel processo = buscarProcesso(prcCod);
        if (processos.existsByVerCodAndOrdemAndPrcCodNot(processo.getVerCod(), dto.ordem(), prcCod)) {
            throw new RegraNegocioException("Já existe um processo com a ordem " + dto.ordem() + ".");
        }
        mapper.atualizar(dto, processo);
        processos.save(processo);
        marcarRascunhoAlterado();
        return montarProcesso(processo);
    }

    /** Ativa/inativa o processo. Inativo continua visível, mas não entra na criação de modelos. */
    @Transactional(rollbackFor = Exception.class)
    public ProcessoDTO alterarSituacaoProcesso(Long prcCod, EAtivoInativo situacao) {
        ProcessoModel processo = buscarProcesso(prcCod);
        processo.setSituacao(situacao);
        processos.save(processo);
        marcarRascunhoAlterado();
        return montarProcesso(processo);
    }

    // ---- práticas ----

    /** Adiciona uma prática ao processo (accordion expandido). */
    @Transactional(rollbackFor = Exception.class)
    public PraticaDTO adicionarPratica(Long prcCod, PraticaDTO dto) {
        buscarProcesso(prcCod);
        PraticaModel pratica = mapper.toModel(dto);
        pratica.setPrcCod(prcCod);
        pratica.setSituacao(EAtivoInativo.ATIVO);
        praticas.save(pratica);
        marcarRascunhoAlterado();
        return mapper.toDTO(pratica);
    }

    /** Edita nome/descrição da prática (validando o vínculo com o processo). */
    @Transactional(rollbackFor = Exception.class)
    public PraticaDTO editarPratica(Long prcCod, Long prtCod, PraticaDTO dto) {
        PraticaModel pratica = buscarPratica(prcCod, prtCod);
        mapper.atualizar(dto, pratica);
        praticas.save(pratica);
        marcarRascunhoAlterado();
        return mapper.toDTO(pratica);
    }

    /** Ativa/inativa a prática. */
    @Transactional(rollbackFor = Exception.class)
    public PraticaDTO alterarSituacaoPratica(Long prcCod, Long prtCod, EAtivoInativo situacao) {
        PraticaModel pratica = buscarPratica(prcCod, prtCod);
        pratica.setSituacao(situacao);
        praticas.save(pratica);
        marcarRascunhoAlterado();
        return mapper.toDTO(pratica);
    }

    // ---- indicadores ----

    /**
     * Indicadores da versão. O indicador aponta para uma prática (PRT_COD), então reúno as práticas
     * da versão (processos → práticas) e busco os indicadores delas numa só query; o mesmo mapa de
     * nomes preenche o "Vínculo metodológico".
     */
    @Transactional(readOnly = true)
    public List<IndicadorDTO> listarIndicadores(Long verCod) {
        List<Long> prcCods = processos.findByVerCodOrderByOrdemAscPrcCodAsc(verCod).stream()
                .map(ProcessoModel::getPrcCod).toList();
        if (prcCods.isEmpty()) {
            return List.of();
        }
        Map<Long, String> nomePorPratica = praticas.findByPrcCodIn(prcCods).stream()
                .collect(Collectors.toMap(PraticaModel::getPrtCod, PraticaModel::getNome));
        List<IndicadorDTO> lista = new ArrayList<>();
        for (IndicadorMetodologiaModel ind : indicadores.findByPrtCodInOrderByNomeAsc(nomePorPratica.keySet())) {
            lista.add(mapper.toDTO(ind, nomePorPratica.get(ind.getPrtCod())));
        }
        return lista;
    }

    @Transactional(rollbackFor = Exception.class)
    public IndicadorDTO criarIndicador(IndicadorDTO dto) {
        PraticaModel pratica = buscarPraticaPorId(dto.prtCod());
        IndicadorMetodologiaModel indicador = mapper.toModel(dto);
        indicador.setSituacao(EAtivoInativo.ATIVO);
        indicadores.save(indicador);
        marcarRascunhoAlterado();
        return mapper.toDTO(indicador, pratica.getNome());
    }

    @Transactional(rollbackFor = Exception.class)
    public IndicadorDTO editarIndicador(Long inmCod, IndicadorDTO dto) {
        IndicadorMetodologiaModel indicador = buscarIndicador(inmCod);
        PraticaModel pratica = buscarPraticaPorId(dto.prtCod());
        mapper.atualizar(dto, indicador);
        indicadores.save(indicador);
        marcarRascunhoAlterado();
        return mapper.toDTO(indicador, pratica.getNome());
    }

    /** Ativa/inativa o indicador. */
    @Transactional(rollbackFor = Exception.class)
    public IndicadorDTO alterarSituacaoIndicador(Long inmCod, EAtivoInativo situacao) {
        IndicadorMetodologiaModel indicador = buscarIndicador(inmCod);
        indicador.setSituacao(situacao);
        indicadores.save(indicador);
        marcarRascunhoAlterado();
        String vinculo = buscarPraticaPorId(indicador.getPrtCod()).getNome();
        return mapper.toDTO(indicador, vinculo);
    }

    // ---- apoio: versão ----

    private VersaoMetodologiaModel obterOuCriarRascunho() {
        return versoes.findFirstBySituacaoOrderByVerCodDesc(ESituacaoVersao.RASCUNHO)
                .orElseGet(() -> {
                    VersaoMetodologiaModel v = new VersaoMetodologiaModel();
                    v.setVersao("rascunho");
                    v.setSituacao(ESituacaoVersao.RASCUNHO);
                    v.setAlterada(false);
                    return versoes.save(v);
                });
    }

    /** Liga o flag de "alterações não publicadas" no rascunho (chamado após cada mutação). */
    private void marcarRascunhoAlterado() {
        versoes.findFirstBySituacaoOrderByVerCodDesc(ESituacaoVersao.RASCUNHO).ifPresent(rascunho -> {
            if (!rascunho.isAlterada()) {
                rascunho.setAlterada(true);
                versoes.save(rascunho);
            }
        });
    }

    /** Deep-copy da árvore do rascunho para uma nova versão VIGENTE (imutável). */
    private VersaoMetodologiaModel clonarComoVigente(VersaoMetodologiaModel rascunho, String rotulo,
                                                     Long pubPesCod) {
        VersaoMetodologiaModel publicada = new VersaoMetodologiaModel();
        publicada.setVersao(rotulo);
        publicada.setSituacao(ESituacaoVersao.VIGENTE);
        publicada.setPublicadaEm(LocalDateTime.now());
        publicada.setPubPesCod(pubPesCod);
        versoes.save(publicada);

        for (ProcessoModel proc : processos.findByVerCodOrderByOrdemAscPrcCodAsc(rascunho.getVerCod())) {
            ProcessoModel np = new ProcessoModel();
            np.setVerCod(publicada.getVerCod());
            np.setOrdem(proc.getOrdem());
            np.setNome(proc.getNome());
            np.setDescricao(proc.getDescricao());
            np.setSituacao(proc.getSituacao());
            processos.save(np);

            for (PraticaModel pr : praticas.findByPrcCodOrderByPrtCodAsc(proc.getPrcCod())) {
                PraticaModel npr = new PraticaModel();
                npr.setPrcCod(np.getPrcCod());
                npr.setNome(pr.getNome());
                npr.setDescricao(pr.getDescricao());
                npr.setSituacao(pr.getSituacao());
                praticas.save(npr);

                for (IndicadorMetodologiaModel ind : indicadores.findByPrtCod(pr.getPrtCod())) {
                    IndicadorMetodologiaModel nind = new IndicadorMetodologiaModel();
                    nind.setPrtCod(npr.getPrtCod());
                    nind.setNome(ind.getNome());
                    nind.setUnidade(ind.getUnidade());
                    nind.setPeriodicidade(ind.getPeriodicidade());
                    nind.setSituacao(ind.getSituacao());
                    indicadores.save(nind);
                }
            }
        }
        return publicada;
    }

    private void exigirVersao(Long verCod) {
        if (!versoes.existsById(verCod)) {
            throw new NaoEncontradoException("Versão da metodologia", verCod);
        }
    }

    /** Pessoa (PES_COD) do usuário logado neste tenant, ou {@code null} se não resolvível. */
    private Long pessoaAtual() {
        Long ctaCod = SessaoContext.contaAtual();
        if (ctaCod == null) {
            return null;
        }
        return pessoas.findByCtaCod(ctaCod).map(PessoasModel::getPesCod).orElse(null);
    }

    /** Nome de quem publicou (pessoa → conta), ou {@code null}. */
    private String nomePessoa(Long pesCod) {
        if (pesCod == null) {
            return null;
        }
        PessoasModel pessoa = pessoas.findById(pesCod).orElse(null);
        if (pessoa == null) {
            return null;
        }
        return contas.findById(pessoa.getCtaCod()).map(ContasModel::getNome).orElse(null);
    }

    // ---- apoio: processos/práticas/indicadores ----

    private ProcessoModel buscarProcesso(Long prcCod) {
        return processos.findById(prcCod)
                .orElseThrow(() -> new NaoEncontradoException("Processo", prcCod));
    }

    /** Busca a prática garantindo que ela pertence ao processo informado. */
    private PraticaModel buscarPratica(Long prcCod, Long prtCod) {
        PraticaModel pratica = praticas.findById(prtCod)
                .orElseThrow(() -> new NaoEncontradoException("Prática", prtCod));
        if (!pratica.getPrcCod().equals(prcCod)) {
            throw new RegraNegocioException("A prática não pertence a este processo.");
        }
        return pratica;
    }

    private PraticaModel buscarPraticaPorId(Long prtCod) {
        return praticas.findById(prtCod)
                .orElseThrow(() -> new NaoEncontradoException("Prática", prtCod));
    }

    private IndicadorMetodologiaModel buscarIndicador(Long inmCod) {
        return indicadores.findById(inmCod)
                .orElseThrow(() -> new NaoEncontradoException("Indicador", inmCod));
    }

    /** Orquestra a busca das práticas do processo; a montagem do DTO fica no mapper. */
    private ProcessoDTO montarProcesso(ProcessoModel processo) {
        List<PraticaDTO> lista = mapper.toDTOList(praticas.findByPrcCodOrderByPrtCodAsc(processo.getPrcCod()));
        return mapper.toDTO(processo, lista);
    }
}

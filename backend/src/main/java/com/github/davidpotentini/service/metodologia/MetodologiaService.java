package com.github.davidpotentini.service.metodologia;

import com.github.davidpotentini.comum.erro.NaoEncontradoException;
import com.github.davidpotentini.comum.erro.RegraNegocioException;
import com.github.davidpotentini.dto.metodologia.AtividadeMetodologiaDTO;
import com.github.davidpotentini.dto.metodologia.IndicadorDTO;
import com.github.davidpotentini.dto.metodologia.PraticaDTO;
import com.github.davidpotentini.dto.metodologia.ProcessoDTO;
import com.github.davidpotentini.enums.EAtivoInativo;
import com.github.davidpotentini.mapper.metodologia.MetodologiaMapper;
import com.github.davidpotentini.model.metodologia.AtividadeMetodologiaModel;
import com.github.davidpotentini.model.metodologia.IndicadorMetodologiaModel;
import com.github.davidpotentini.model.metodologia.PraticaModel;
import com.github.davidpotentini.model.metodologia.ProcessoModel;
import com.github.davidpotentini.repository.metodologia.AtividadeMetodologiaRepository;
import com.github.davidpotentini.repository.metodologia.IndicadorMetodologiaRepository;
import com.github.davidpotentini.repository.metodologia.PraticaRepository;
import com.github.davidpotentini.repository.metodologia.ProcessoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Metodologia CERNE da incubadora logada. Roda no schema do próprio tenant (o JWT já deixou o
 * {@code TenantContext} ativo), então as tabelas são lidas/gravadas direto.
 *
 * <p>Documento vivo (sem versionamento): a árvore {@code processos → práticas → indicadores} é única
 * e sempre editável. Qualquer correção passa a valer na hora para os modelos e planos que a leem ao
 * vivo. A criação de modelos e a geração de indicadores do ciclo consomem esta metodologia.
 */
@Service
public class MetodologiaService {

    private final ProcessoRepository processos;
    private final PraticaRepository praticas;
    private final IndicadorMetodologiaRepository indicadores;
    private final AtividadeMetodologiaRepository atividades;
    private final MetodologiaMapper mapper;

    public MetodologiaService(ProcessoRepository processos, PraticaRepository praticas,
                              IndicadorMetodologiaRepository indicadores,
                              AtividadeMetodologiaRepository atividades, MetodologiaMapper mapper) {
        this.processos = processos;
        this.praticas = praticas;
        this.indicadores = indicadores;
        this.atividades = atividades;
        this.mapper = mapper;
    }

    // ---- processos ----

    /** Processos da metodologia (accordions ordenados por {@code ordem}), cada um com suas práticas. */
    @Transactional(readOnly = true)
    public List<ProcessoDTO> listarProcessos() {
        List<ProcessoDTO> lista = new ArrayList<>();
        for (ProcessoModel processo : processos.findAllByOrderByOrdemAscPrcCodAsc()) {
            lista.add(montarProcesso(processo));
        }
        return lista;
    }

    /** Novo processo, anexado no fim (a ordem é definida depois por arrastar). */
    @Transactional(rollbackFor = Exception.class)
    public ProcessoDTO criarProcesso(ProcessoDTO dto) {
        ProcessoModel processo = mapper.toModel(dto);
        processo.setOrdem(proximaOrdem());
        processo.setSituacao(EAtivoInativo.ATIVO);
        processos.save(processo);
        return mapper.toDTO(processo, List.of());
    }

    /** Edita nome/descrição do processo (a ordem é gerida por arrastar). */
    @Transactional(rollbackFor = Exception.class)
    public ProcessoDTO editarProcesso(Long prcCod, ProcessoDTO dto) {
        ProcessoModel processo = buscarProcesso(prcCod);
        mapper.atualizar(dto, processo);
        processos.save(processo);
        return montarProcesso(processo);
    }

    /**
     * Reordena os processos conforme a sequência de {@code prcCods} (arrastar-e-soltar): a posição na
     * lista vira a nova {@code ordem}. A lista deve conter exatamente os processos existentes.
     */
    @Transactional(rollbackFor = Exception.class)
    public List<ProcessoDTO> reordenarProcessos(List<Long> prcCods) {
        List<ProcessoModel> todos = processos.findAllByOrderByOrdemAscPrcCodAsc();
        Map<Long, ProcessoModel> porId = new HashMap<>();
        for (ProcessoModel processo : todos) {
            porId.put(processo.getPrcCod(), processo);
        }
        if (prcCods == null || prcCods.size() != todos.size()) {
            throw new RegraNegocioException("A ordenação deve conter exatamente os processos existentes.");
        }
        int ordem = 1;
        Set<Long> vistos = new HashSet<>();
        for (Long prcCod : prcCods) {
            ProcessoModel processo = porId.get(prcCod);
            if (processo == null || !vistos.add(prcCod)) {
                throw new RegraNegocioException("A ordenação deve conter exatamente os processos existentes.");
            }
            processo.setOrdem(ordem++);
        }
        processos.saveAll(todos);
        return listarProcessos();
    }

    /** Ativa/inativa o processo. Inativo continua visível, mas não entra na criação de modelos. */
    @Transactional(rollbackFor = Exception.class)
    public ProcessoDTO alterarSituacaoProcesso(Long prcCod, EAtivoInativo situacao) {
        ProcessoModel processo = buscarProcesso(prcCod);
        processo.setSituacao(situacao);
        processos.save(processo);
        return montarProcesso(processo);
    }

    private int proximaOrdem() {
        ProcessoModel ultimo = processos.findFirstByOrderByOrdemDesc().orElse(null);
        return ultimo == null ? 1 : ultimo.getOrdem() + 1;
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
        return mapper.toDTO(pratica);
    }

    /** Edita nome/descrição da prática (validando o vínculo com o processo). */
    @Transactional(rollbackFor = Exception.class)
    public PraticaDTO editarPratica(Long prcCod, Long prtCod, PraticaDTO dto) {
        PraticaModel pratica = buscarPratica(prcCod, prtCod);
        mapper.atualizar(dto, pratica);
        praticas.save(pratica);
        return mapper.toDTO(pratica);
    }

    /** Ativa/inativa a prática. */
    @Transactional(rollbackFor = Exception.class)
    public PraticaDTO alterarSituacaoPratica(Long prcCod, Long prtCod, EAtivoInativo situacao) {
        PraticaModel pratica = buscarPratica(prcCod, prtCod);
        pratica.setSituacao(situacao);
        praticas.save(pratica);
        return mapper.toDTO(pratica);
    }

    // ---- indicadores ----

    /**
     * Indicadores da metodologia. O indicador aponta para uma prática (PRT_COD), então reúno as
     * práticas (processos → práticas) e busco os indicadores delas numa só query; o mesmo mapa de
     * nomes preenche o "Vínculo metodológico".
     */
    @Transactional(readOnly = true)
    public List<IndicadorDTO> listarIndicadores() {
        List<Long> prcCods = new ArrayList<>();
        for (ProcessoModel processo : processos.findAllByOrderByOrdemAscPrcCodAsc()) {
            prcCods.add(processo.getPrcCod());
        }
        if (prcCods.isEmpty()) {
            return List.of();
        }
        Map<Long, String> nomePorPratica = new HashMap<>();
        for (PraticaModel pratica : praticas.findByPrcCodIn(prcCods)) {
            nomePorPratica.put(pratica.getPrtCod(), pratica.getNome());
        }
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
        return mapper.toDTO(indicador, pratica.getNome());
    }

    @Transactional(rollbackFor = Exception.class)
    public IndicadorDTO editarIndicador(Long inmCod, IndicadorDTO dto) {
        IndicadorMetodologiaModel indicador = buscarIndicador(inmCod);
        PraticaModel pratica = buscarPraticaPorId(dto.prtCod());
        mapper.atualizar(dto, indicador);
        indicadores.save(indicador);
        return mapper.toDTO(indicador, pratica.getNome());
    }

    /** Ativa/inativa o indicador. */
    @Transactional(rollbackFor = Exception.class)
    public IndicadorDTO alterarSituacaoIndicador(Long inmCod, EAtivoInativo situacao) {
        IndicadorMetodologiaModel indicador = buscarIndicador(inmCod);
        indicador.setSituacao(situacao);
        indicadores.save(indicador);
        String vinculo = buscarPraticaPorId(indicador.getPrtCod()).getNome();
        return mapper.toDTO(indicador, vinculo);
    }

    // ---- atividades ----

    /**
     * Atividades-padrão da metodologia. A atividade aponta para uma prática (PRT_COD); reúno as
     * práticas (processos → práticas) e busco as atividades delas numa só query; o mesmo mapa de nomes
     * preenche o "Vínculo metodológico".
     */
    @Transactional(readOnly = true)
    public List<AtividadeMetodologiaDTO> listarAtividades() {
        List<Long> prcCods = new ArrayList<>();
        for (ProcessoModel processo : processos.findAllByOrderByOrdemAscPrcCodAsc()) {
            prcCods.add(processo.getPrcCod());
        }
        if (prcCods.isEmpty()) {
            return List.of();
        }
        Map<Long, String> nomePorPratica = new HashMap<>();
        for (PraticaModel pratica : praticas.findByPrcCodIn(prcCods)) {
            nomePorPratica.put(pratica.getPrtCod(), pratica.getNome());
        }
        List<AtividadeMetodologiaDTO> lista = new ArrayList<>();
        for (AtividadeMetodologiaModel atv : atividades.findByPrtCodInOrderByNomeAsc(nomePorPratica.keySet())) {
            lista.add(mapper.toDTO(atv, nomePorPratica.get(atv.getPrtCod())));
        }
        return lista;
    }

    @Transactional(rollbackFor = Exception.class)
    public AtividadeMetodologiaDTO criarAtividade(AtividadeMetodologiaDTO dto) {
        PraticaModel pratica = buscarPraticaPorId(dto.prtCod());
        AtividadeMetodologiaModel atividade = mapper.toModel(dto);
        atividade.setSituacao(EAtivoInativo.ATIVO);
        atividades.save(atividade);
        return mapper.toDTO(atividade, pratica.getNome());
    }

    @Transactional(rollbackFor = Exception.class)
    public AtividadeMetodologiaDTO editarAtividade(Long ameCod, AtividadeMetodologiaDTO dto) {
        AtividadeMetodologiaModel atividade = buscarAtividade(ameCod);
        PraticaModel pratica = buscarPraticaPorId(dto.prtCod());
        mapper.atualizar(dto, atividade);
        atividades.save(atividade);
        return mapper.toDTO(atividade, pratica.getNome());
    }

    /** Ativa/inativa a atividade. */
    @Transactional(rollbackFor = Exception.class)
    public AtividadeMetodologiaDTO alterarSituacaoAtividade(Long ameCod, EAtivoInativo situacao) {
        AtividadeMetodologiaModel atividade = buscarAtividade(ameCod);
        atividade.setSituacao(situacao);
        atividades.save(atividade);
        String vinculo = buscarPraticaPorId(atividade.getPrtCod()).getNome();
        return mapper.toDTO(atividade, vinculo);
    }

    /**
     * Exclui a atividade-padrão. Diferente de processos/práticas (que só se inativam), a atividade é
     * um dado da incubadora e pode ser removida — o que já foi materializado num ciclo não é afetado
     * (a cópia do ciclo é independente).
     */
    @Transactional(rollbackFor = Exception.class)
    public void excluirAtividade(Long ameCod) {
        atividades.delete(buscarAtividade(ameCod));
    }

    // ---- apoio ----

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

    private AtividadeMetodologiaModel buscarAtividade(Long ameCod) {
        return atividades.findById(ameCod)
                .orElseThrow(() -> new NaoEncontradoException("Atividade", ameCod));
    }

    /** Orquestra a busca das práticas do processo; a montagem do DTO fica no mapper. */
    private ProcessoDTO montarProcesso(ProcessoModel processo) {
        List<PraticaDTO> lista = mapper.toDTOList(praticas.findByPrcCodOrderByPrtCodAsc(processo.getPrcCod()));
        return mapper.toDTO(processo, lista);
    }
}

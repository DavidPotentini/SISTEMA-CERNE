package com.github.davidpotentini.service.metodologia;

import com.github.davidpotentini.comum.erro.NaoEncontradoException;
import com.github.davidpotentini.comum.erro.RegraNegocioException;
import com.github.davidpotentini.dto.metodologia.AgrupamentoDTO;
import com.github.davidpotentini.dto.metodologia.AtividadeMetodologiaDTO;
import com.github.davidpotentini.dto.metodologia.IndicadorDTO;
import com.github.davidpotentini.dto.metodologia.PraticaDTO;
import com.github.davidpotentini.dto.metodologia.ProcessoDTO;
import com.github.davidpotentini.enums.EAtivoInativo;
import com.github.davidpotentini.mapper.metodologia.MetodologiaMapper;
import com.github.davidpotentini.model.metodologia.AgrupamentoModel;
import com.github.davidpotentini.model.metodologia.AtividadeMetodologiaModel;
import com.github.davidpotentini.model.metodologia.IndicadorMetodologiaModel;
import com.github.davidpotentini.model.metodologia.PraticaModel;
import com.github.davidpotentini.model.metodologia.ProcessoModel;
import com.github.davidpotentini.repository.metodologia.AgrupamentoRepository;
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
 * e sempre editável. Qualquer correção passa a valer na hora para os planos que a leem ao vivo. A
 * geração do planejamento e a dos indicadores do ciclo consomem esta metodologia.
 */
@Service
public class MetodologiaService {

    private final ProcessoRepository processos;
    private final PraticaRepository praticas;
    private final AgrupamentoRepository agrupamentos;
    private final IndicadorMetodologiaRepository indicadores;
    private final AtividadeMetodologiaRepository atividades;
    private final MetodologiaMapper mapper;

    public MetodologiaService(ProcessoRepository processos, PraticaRepository praticas,
                              AgrupamentoRepository agrupamentos,
                              IndicadorMetodologiaRepository indicadores,
                              AtividadeMetodologiaRepository atividades, MetodologiaMapper mapper) {
        this.processos = processos;
        this.praticas = praticas;
        this.agrupamentos = agrupamentos;
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

    /** Ativa/inativa o processo. Inativo continua visível, mas não entra na geração do planejamento. */
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
        pratica.setOrdem(proximaOrdemPratica(prcCod));
        pratica.setSituacao(EAtivoInativo.ATIVO);
        praticas.save(pratica);
        return mapper.toDTO(pratica);
    }

    /** Próxima {@code ordem} dentro do processo (anexa a prática no fim). */
    private int proximaOrdemPratica(Long prcCod) {
        PraticaModel ultima = praticas.findFirstByPrcCodOrderByOrdemDesc(prcCod).orElse(null);
        return ultima == null ? 1 : ultima.getOrdem() + 1;
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

    /**
     * Reordena as práticas de um processo conforme a sequência de {@code prtCods} (arrastar-e-soltar):
     * a posição na lista vira a nova {@code ordem}. A lista deve conter exatamente as práticas do processo.
     */
    @Transactional(rollbackFor = Exception.class)
    public List<ProcessoDTO> reordenarPraticas(Long prcCod, List<Long> prtCods) {
        buscarProcesso(prcCod);
        List<PraticaModel> todas = praticas.findByPrcCodOrderByOrdemAscPrtCodAsc(prcCod);
        Map<Long, PraticaModel> porId = new HashMap<>();
        for (PraticaModel pratica : todas) {
            porId.put(pratica.getPrtCod(), pratica);
        }
        if (prtCods == null || prtCods.size() != todas.size()) {
            throw new RegraNegocioException("A ordenação deve conter exatamente as práticas do processo.");
        }
        int ordem = 1;
        Set<Long> vistos = new HashSet<>();
        for (Long prtCod : prtCods) {
            PraticaModel pratica = porId.get(prtCod);
            if (pratica == null || !vistos.add(prtCod)) {
                throw new RegraNegocioException("A ordenação deve conter exatamente as práticas do processo.");
            }
            pratica.setOrdem(ordem++);
        }
        praticas.saveAll(todas);
        return listarProcessos();
    }

    // ---- agrupamentos ----

    /**
     * Agrupamentos (sub-planos) da metodologia. O agrupamento aponta para uma prática (PRT_COD); reúno
     * as práticas (processos → práticas) e busco os agrupamentos delas numa só query; o mesmo mapa de
     * nomes preenche o "Vínculo metodológico".
     */
    @Transactional(readOnly = true)
    public List<AgrupamentoDTO> listarAgrupamentos() {
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
        List<AgrupamentoDTO> lista = new ArrayList<>();
        for (AgrupamentoModel grupo : agrupamentos.findByPrtCodInOrderByOrdemAscAgrCodAsc(nomePorPratica.keySet())) {
            lista.add(mapper.toDTO(grupo, nomePorPratica.get(grupo.getPrtCod())));
        }
        return lista;
    }

    /** Adiciona um agrupamento à prática (anexado no fim). */
    @Transactional(rollbackFor = Exception.class)
    public AgrupamentoDTO adicionarAgrupamento(Long prtCod, AgrupamentoDTO dto) {
        PraticaModel pratica = buscarPraticaPorId(prtCod);
        AgrupamentoModel grupo = mapper.toModel(dto);
        grupo.setPrtCod(prtCod);
        grupo.setOrdem(proximaOrdemAgrupamento(prtCod));
        grupo.setSituacao(EAtivoInativo.ATIVO);
        agrupamentos.save(grupo);
        return mapper.toDTO(grupo, pratica.getNome());
    }

    /** Próxima {@code ordem} dentro da prática (anexa o agrupamento no fim). */
    private int proximaOrdemAgrupamento(Long prtCod) {
        AgrupamentoModel ultimo = agrupamentos.findFirstByPrtCodOrderByOrdemDesc(prtCod).orElse(null);
        return ultimo == null ? 1 : ultimo.getOrdem() + 1;
    }

    /** Edita nome/descrição do agrupamento. */
    @Transactional(rollbackFor = Exception.class)
    public AgrupamentoDTO editarAgrupamento(Long agrCod, AgrupamentoDTO dto) {
        AgrupamentoModel grupo = buscarAgrupamento(agrCod);
        mapper.atualizar(dto, grupo);
        agrupamentos.save(grupo);
        return mapper.toDTO(grupo, buscarPraticaPorId(grupo.getPrtCod()).getNome());
    }

    /**
     * Reordena os agrupamentos de uma prática conforme a sequência de {@code agrCods} (arrastar-e-soltar):
     * a posição na lista vira a nova {@code ordem}. A lista deve conter exatamente os agrupamentos da prática.
     */
    @Transactional(rollbackFor = Exception.class)
    public List<AgrupamentoDTO> reordenarAgrupamentos(Long prtCod, List<Long> agrCods) {
        buscarPraticaPorId(prtCod);
        List<AgrupamentoModel> todos = agrupamentos.findByPrtCodOrderByOrdemAscAgrCodAsc(prtCod);
        Map<Long, AgrupamentoModel> porId = new HashMap<>();
        for (AgrupamentoModel grupo : todos) {
            porId.put(grupo.getAgrCod(), grupo);
        }
        if (agrCods == null || agrCods.size() != todos.size()) {
            throw new RegraNegocioException("A ordenação deve conter exatamente os agrupamentos da prática.");
        }
        int ordem = 1;
        Set<Long> vistos = new HashSet<>();
        for (Long agrCod : agrCods) {
            AgrupamentoModel grupo = porId.get(agrCod);
            if (grupo == null || !vistos.add(agrCod)) {
                throw new RegraNegocioException("A ordenação deve conter exatamente os agrupamentos da prática.");
            }
            grupo.setOrdem(ordem++);
        }
        agrupamentos.saveAll(todos);
        return listarAgrupamentos();
    }

    /** Ativa/inativa o agrupamento. */
    @Transactional(rollbackFor = Exception.class)
    public AgrupamentoDTO alterarSituacaoAgrupamento(Long agrCod, EAtivoInativo situacao) {
        AgrupamentoModel grupo = buscarAgrupamento(agrCod);
        grupo.setSituacao(situacao);
        agrupamentos.save(grupo);
        return mapper.toDTO(grupo, buscarPraticaPorId(grupo.getPrtCod()).getNome());
    }

    /** Exclui o agrupamento. Bloqueia se ainda houver atividades nele (mova-as ou exclua antes). */
    @Transactional(rollbackFor = Exception.class)
    public void excluirAgrupamento(Long agrCod) {
        AgrupamentoModel grupo = buscarAgrupamento(agrCod);
        if (atividades.existsByAgrCod(agrCod)) {
            throw new RegraNegocioException(
                    "Este agrupamento tem atividades e não pode ser excluído. "
                    + "Mova ou exclua as atividades antes.");
        }
        agrupamentos.delete(grupo);
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
        for (AtividadeMetodologiaModel atv : atividades.findByPrtCodInOrderByOrdemAscNomeAsc(nomePorPratica.keySet())) {
            lista.add(mapper.toDTO(atv, nomePorPratica.get(atv.getPrtCod())));
        }
        return lista;
    }

    @Transactional(rollbackFor = Exception.class)
    public AtividadeMetodologiaDTO criarAtividade(AtividadeMetodologiaDTO dto) {
        PraticaModel pratica = buscarPraticaPorId(dto.prtCod());
        AtividadeMetodologiaModel atividade = mapper.toModel(dto);
        atividade.setOrdem(proximaOrdemAtividade(dto.prtCod()));
        atividade.setSituacao(EAtivoInativo.ATIVO);
        atividades.save(atividade);
        return mapper.toDTO(atividade, pratica.getNome());
    }

    /** Próxima {@code ordem} dentro da prática (anexa a atividade no fim). */
    private int proximaOrdemAtividade(Long prtCod) {
        AtividadeMetodologiaModel ultima = atividades.findFirstByPrtCodOrderByOrdemDesc(prtCod).orElse(null);
        return ultima == null ? 1 : ultima.getOrdem() + 1;
    }

    @Transactional(rollbackFor = Exception.class)
    public AtividadeMetodologiaDTO editarAtividade(Long ameCod, AtividadeMetodologiaDTO dto) {
        AtividadeMetodologiaModel atividade = buscarAtividade(ameCod);
        PraticaModel pratica = buscarPraticaPorId(dto.prtCod());
        mapper.atualizar(dto, atividade);
        atividades.save(atividade);
        return mapper.toDTO(atividade, pratica.getNome());
    }

    /**
     * Reordena as atividades de uma prática conforme a sequência de {@code ameCods} (arrastar-e-soltar):
     * a posição na lista vira a nova {@code ordem}. A lista deve conter exatamente as atividades da prática.
     */
    @Transactional(rollbackFor = Exception.class)
    public List<AtividadeMetodologiaDTO> reordenarAtividades(Long prtCod, List<Long> ameCods) {
        buscarPraticaPorId(prtCod);
        List<AtividadeMetodologiaModel> todas = atividades.findByPrtCod(prtCod);
        Map<Long, AtividadeMetodologiaModel> porId = new HashMap<>();
        for (AtividadeMetodologiaModel atv : todas) {
            porId.put(atv.getAmeCod(), atv);
        }
        if (ameCods == null || ameCods.size() != todas.size()) {
            throw new RegraNegocioException("A ordenação deve conter exatamente as atividades da prática.");
        }
        int ordem = 1;
        Set<Long> vistos = new HashSet<>();
        for (Long ameCod : ameCods) {
            AtividadeMetodologiaModel atv = porId.get(ameCod);
            if (atv == null || !vistos.add(ameCod)) {
                throw new RegraNegocioException("A ordenação deve conter exatamente as atividades da prática.");
            }
            atv.setOrdem(ordem++);
        }
        atividades.saveAll(todas);
        return listarAtividades();
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

    /** Marca/desmarca a atividade como "da incubada" (repete por empreendimento na geração do ciclo). */
    @Transactional(rollbackFor = Exception.class)
    public AtividadeMetodologiaDTO alterarPorEmpreendimentoAtividade(Long ameCod, boolean valor) {
        AtividadeMetodologiaModel atividade = buscarAtividade(ameCod);
        atividade.setPorEmpreendimento(valor);
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

    private AgrupamentoModel buscarAgrupamento(Long agrCod) {
        return agrupamentos.findById(agrCod)
                .orElseThrow(() -> new NaoEncontradoException("Agrupamento", agrCod));
    }

    /** Orquestra a busca das práticas do processo; a montagem do DTO fica no mapper. */
    private ProcessoDTO montarProcesso(ProcessoModel processo) {
        List<PraticaDTO> lista = mapper.toDTOList(praticas.findByPrcCodOrderByOrdemAscPrtCodAsc(processo.getPrcCod()));
        return mapper.toDTO(processo, lista);
    }
}

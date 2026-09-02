package com.github.davidpotentini.service.planejamento;

import com.github.davidpotentini.comum.erro.NaoEncontradoException;
import com.github.davidpotentini.comum.erro.RegraNegocioException;
import com.github.davidpotentini.comum.tenant.SessaoContext;
import com.github.davidpotentini.dto.planejamento.AtividadePlanejadaDTO;
import com.github.davidpotentini.dto.planejamento.PlanGrupoDTO;
import com.github.davidpotentini.dto.planejamento.PlanPraticaDTO;
import com.github.davidpotentini.dto.planejamento.PlanProcessoDTO;
import com.github.davidpotentini.dto.planejamento.PlanejamentoAtualDTO;
import com.github.davidpotentini.dto.planejamento.PlanejamentoDTO;
import com.github.davidpotentini.enums.EAtivoInativo;
import com.github.davidpotentini.enums.EOrigemAtividade;
import com.github.davidpotentini.enums.EStatusAtividade;
import com.github.davidpotentini.enums.EStatusPlanejamento;
import com.github.davidpotentini.mapper.planejamento.PlanejamentoMapper;
import com.github.davidpotentini.comum.ciclo.CicloContexto;
import com.github.davidpotentini.model.ciclos.CiclosModel;
import com.github.davidpotentini.model.contas.ContasModel;
import com.github.davidpotentini.model.empreendimentos.EmpreendimentosModel;
import com.github.davidpotentini.model.estruturaciclo.AgrupamentoCicloModel;
import com.github.davidpotentini.model.estruturaciclo.PraticaCicloModel;
import com.github.davidpotentini.model.estruturaciclo.ProcessoCicloModel;
import com.github.davidpotentini.model.metodologia.AtividadeMetodologiaModel;
import com.github.davidpotentini.model.pessoas.PessoasModel;
import com.github.davidpotentini.model.planejamento.AtividadePlanejadaModel;
import com.github.davidpotentini.model.planejamento.PlanejamentoModel;
import com.github.davidpotentini.repository.ciclos.CiclosRepository;
import com.github.davidpotentini.repository.contas.ContasRepository;
import com.github.davidpotentini.repository.empreendimentos.EmpreendimentosRepository;
import com.github.davidpotentini.repository.evidencia.EvidenciaRepository;
import com.github.davidpotentini.repository.estruturaciclo.AgrupamentoCicloRepository;
import com.github.davidpotentini.repository.estruturaciclo.PraticaCicloRepository;
import com.github.davidpotentini.repository.estruturaciclo.ProcessoCicloRepository;
import com.github.davidpotentini.repository.metodologia.AtividadeMetodologiaRepository;
import com.github.davidpotentini.repository.pessoas.PessoasRepository;
import com.github.davidpotentini.repository.planejamento.AtividadePlanejadaRepository;
import com.github.davidpotentini.repository.planejamento.PlanejamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Planejamento institucional do ciclo ativo (schema do tenant vem do JWT).
 *
 * <p>Há no máx. um planejamento vigente ({@code PUBLICADO}) por ciclo. "Gerar do ciclo" cria um
 * planejamento para o ciclo em foco copiando as atividades ATIVAS da metodologia vigente; se já houver
 * um vigente, ele é <b>substituído</b> — o anterior é inativado ({@code ENCERRADO}) e fica como
 * histórico. A estrutura de processos/práticas é a instância do ciclo (não copiada); as atividades
 * copiadas podem ser ajustadas e complementares podem ser incluídas.
 */
@Service
public class PlanejamentoService {

    private final PlanejamentoRepository planejamentos;
    private final AtividadePlanejadaRepository atividades;
    private final CiclosRepository ciclos;
    private final CicloContexto cicloContexto;
    private final AtividadeMetodologiaRepository atividadesMetodologia;
    private final EmpreendimentosRepository empreendimentos;
    private final ProcessoCicloRepository processosCiclo;
    private final PraticaCicloRepository praticasCiclo;
    private final AgrupamentoCicloRepository agrupamentosCiclo;
    private final EvidenciaRepository evidencias;
    private final PessoasRepository pessoas;
    private final ContasRepository contas;
    private final PlanejamentoMapper mapper;

    public PlanejamentoService(PlanejamentoRepository planejamentos,
                               AtividadePlanejadaRepository atividades, CiclosRepository ciclos,
                               CicloContexto cicloContexto,
                               AtividadeMetodologiaRepository atividadesMetodologia,
                               EmpreendimentosRepository empreendimentos,
                               ProcessoCicloRepository processosCiclo, PraticaCicloRepository praticasCiclo,
                               AgrupamentoCicloRepository agrupamentosCiclo, EvidenciaRepository evidencias,
                               PessoasRepository pessoas, ContasRepository contas,
                               PlanejamentoMapper mapper) {
        this.planejamentos = planejamentos;
        this.atividades = atividades;
        this.ciclos = ciclos;
        this.cicloContexto = cicloContexto;
        this.atividadesMetodologia = atividadesMetodologia;
        this.empreendimentos = empreendimentos;
        this.processosCiclo = processosCiclo;
        this.praticasCiclo = praticasCiclo;
        this.agrupamentosCiclo = agrupamentosCiclo;
        this.evidencias = evidencias;
        this.pessoas = pessoas;
        this.contas = contas;
        this.mapper = mapper;
    }

    // ---- planejamento (cabeçalho) ----

    /**
     * Situação do ciclo ativo: se existe e, em caso afirmativo, o planejamento vigente (ou {@code null}
     * quando ainda não foi gerado).
     */
    @Transactional(readOnly = true)
    public PlanejamentoAtualDTO atual() {
        CiclosModel ciclo = cicloContexto.emFoco();
        if (ciclo == null) {
            return new PlanejamentoAtualDTO(false, null, null, null);
        }
        PlanejamentoDTO dto = planejamentoVigente(ciclo.getCicCod())
                .map(this::toDTO)
                .orElse(null);
        return new PlanejamentoAtualDTO(true, ciclo.getCicCod(), ciclo.getNome(), dto);
    }

    /**
     * Materializa o planejamento do ciclo em foco a partir das atividades-padrão da metodologia (parte
     * do "Gerar do ciclo"). Substitui o vigente, se houver (o anterior vira {@code ENCERRADO}). O
     * responsável do plano é o usuário logado. Copia as atividades ATIVAS da metodologia
     * ({@code nome}/{@code observacoes}); "quem"/"quando" ficam nulos, para o ajuste no planejamento.
     *
     * <p>É <b>consumidor</b> da estrutura: casa a prática do template com a do ciclo por proveniência
     * (find-only), sem criar prática — a estrutura já foi materializada por
     * {@link com.github.davidpotentini.service.estruturaciclo.EstruturaCicloService#materializarEstrutura}.
     *
     * <p>Atividades marcadas "da incubada" (pela atividade ou pela prática) são duplicadas por
     * empreendimento de {@code empCods}, cada cópia num grupo dinâmico da incubada (find-or-create em
     * {@code AGRUPAMENTOS_CICLO} por prática+incubada). Sem incubadas, essas atividades não são geradas.
     */
    @Transactional(rollbackFor = Exception.class)
    public PlanejamentoDTO gerarDoCiclo(List<Long> empCods) {
        CiclosModel ciclo = cicloContexto.emFoco();
        if (ciclo == null) {
            throw new RegraNegocioException("Não há ciclo ativo. Abra um ciclo antes de gerar.");
        }

        // Substitui o vigente: o anterior é inativado (ENCERRADO), preservado como histórico.
        planejamentoVigente(ciclo.getCicCod()).ifPresent(anterior -> {
            anterior.setStatus(EStatusPlanejamento.ENCERRADO);
            planejamentos.saveAndFlush(anterior);
        });

        PlanejamentoModel plano = new PlanejamentoModel();
        plano.setNome("Planejamento — " + ciclo.getNome());
        plano.setCicCod(ciclo.getCicCod());
        plano.setStatus(EStatusPlanejamento.PUBLICADO);
        plano.setRespPesCod(pessoaAtual());
        plano.setInicio(ciclo.getInicio());
        plano.setFim(ciclo.getFim());
        planejamentos.save(plano);

        // Índice prática-do-template → prática-do-ciclo (estrutura já materializada); find-only.
        Map<Long, Long> prtcPorPrt = new HashMap<>();
        for (PraticaCicloModel pr : praticasCiclo.findByCicCodOrderByPrtcCodAsc(ciclo.getCicCod())) {
            if (pr.getPrtCodOrigem() != null) {
                prtcPorPrt.put(pr.getPrtCodOrigem(), pr.getPrtcCod());
            }
        }
        // Índice agrupamento-do-template → agrupamento-do-ciclo (estrutura já materializada); find-only.
        Map<Long, Long> agrcPorAgr = new HashMap<>();
        for (AgrupamentoCicloModel g : agrupamentosCiclo.findByCicCodOrderByAgrcCodAsc(ciclo.getCicCod())) {
            if (g.getAgrCodOrigem() != null) {
                agrcPorAgr.put(g.getAgrCodOrigem(), g.getAgrcCod());
            }
        }

        // Incubadas participantes válidas, na ordem dada; nome e ordem do grupo (após os do template).
        List<Long> incubadas = new ArrayList<>();
        Map<Long, String> nomeIncubada = new HashMap<>();
        Map<Long, Integer> ordemIncubada = new HashMap<>();
        if (empCods != null) {
            Set<Long> distintas = new LinkedHashSet<>(empCods);
            for (EmpreendimentosModel e : empreendimentos.findAllById(distintas)) {
                nomeIncubada.put(e.getEmpCod(), e.getNome());
            }
            for (Long empCod : distintas) {
                if (nomeIncubada.containsKey(empCod)) {
                    ordemIncubada.put(empCod, 1000 + incubadas.size());
                    incubadas.add(empCod);
                }
            }
        }

        for (AtividadeMetodologiaModel base
                : atividadesMetodologia.findByPrtCodInOrderByOrdemAscNomeAsc(prtcPorPrt.keySet())) {
            if (base.getSituacao() != EAtivoInativo.ATIVO) {
                continue;
            }
            Long prtcCod = prtcPorPrt.get(base.getPrtCod());
            boolean porEmp = base.isPorEmpreendimento();
            if (!porEmp) {
                Long agrcCod = base.getAgrCod() == null ? null : agrcPorAgr.get(base.getAgrCod());
                salvarCopiaAtividade(plano, base, prtcCod, agrcCod, null);
            } else {
                // "Da incubada": uma cópia por incubada, no grupo dinâmico dela (find-or-create).
                for (Long empCod : incubadas) {
                    Long agrcCod = garantirGrupoEmpreendimento(ciclo.getCicCod(), prtcCod, empCod,
                            nomeIncubada.get(empCod), ordemIncubada.get(empCod));
                    salvarCopiaAtividade(plano, base, prtcCod, agrcCod, empCod);
                }
            }
        }
        return toDTO(plano);
    }

    /** Copia uma atividade-padrão para o planejamento (opcionalmente vinculada a grupo/empreendimento). */
    private void salvarCopiaAtividade(PlanejamentoModel plano, AtividadeMetodologiaModel base,
                                      Long prtcCod, Long agrcCod, Long empCod) {
        AtividadePlanejadaModel atv = new AtividadePlanejadaModel();
        atv.setPlnCod(plano.getPlnCod());
        atv.setOrigem(EOrigemAtividade.METODOLOGIA);
        atv.setPrtcCod(prtcCod);
        atv.setAgrcCod(agrcCod);
        atv.setEmpCod(empCod);
        atv.setOrdem(base.getOrdem());
        atv.setNome(base.getNome());
        atv.setObservacoes(base.getObservacoes());
        atv.setStatus(EStatusAtividade.PLANEJADA);
        atividades.save(atv);
    }

    /**
     * Grupo dinâmico da incubada numa prática do ciclo (find-or-create por {@code (cicCod, prtcCod,
     * empCod)}). Nasce sem origem no template ({@code AGR_COD_ORIGEM} nulo), nome = nome da incubada.
     */
    private Long garantirGrupoEmpreendimento(Long cicCod, Long prtcCod, Long empCod, String nome, int ordem) {
        AgrupamentoCicloModel existente = agrupamentosCiclo
                .findByCicCodAndPrtcCodAndEmpCod(cicCod, prtcCod, empCod).orElse(null);
        if (existente != null) {
            return existente.getAgrcCod();
        }
        AgrupamentoCicloModel g = new AgrupamentoCicloModel();
        g.setCicCod(cicCod);
        g.setPrtcCod(prtcCod);
        g.setEmpCod(empCod);
        g.setOrdem(ordem);
        g.setNome(nome);
        return agrupamentosCiclo.save(g).getAgrcCod();
    }

    /**
     * O planejamento vigente do ciclo já tem edições do usuário? (trava do "Gerar do ciclo": regerar
     * substituiria o plano vigente e perderia esses ajustes). Conta como edição: atividade complementar,
     * responsável ou prazo definidos, execução iniciada (status ≠ PLANEJADA) ou evidência registrada.
     * Sem plano vigente, não há edições.
     */
    @Transactional(readOnly = true)
    public boolean cicloTemEdicoes(Long cicCod) {
        PlanejamentoModel plano = planejamentoVigente(cicCod).orElse(null);
        if (plano == null) {
            return false;
        }
        Long plnCod = plano.getPlnCod();
        return atividades.existsByPlnCodAndOrigem(plnCod, EOrigemAtividade.COMPLEMENTAR)
                || atividades.existsByPlnCodAndRespPesCodNotNull(plnCod)
                || atividades.existsByPlnCodAndPrazoNotNull(plnCod)
                || atividades.existsByPlnCodAndStatusNot(plnCod, EStatusAtividade.PLANEJADA)
                || evidencias.existsByPlanejamento(plnCod);
    }

    // ---- estrutura (processos/práticas herdados + atividades planejadas) ----

    /**
     * Estrutura do planejamento vigente do ciclo ativo: processos e práticas ATIVOS da metodologia
     * (só leitura), cada prática com suas atividades planejadas. Sem planejamento, devolve vazio.
     */
    @Transactional(readOnly = true)
    public List<PlanProcessoDTO> estrutura() {
        PlanejamentoModel plano = vigenteDoCicloAtivo();
        if (plano == null) {
            return List.of();
        }

        Map<Long, String> nomeResponsavel = new HashMap<>();
        // Atividades por agrupamento (AGRC_COD); as sem grupo ficam por prática, para o grupo sintético.
        Map<Long, List<AtividadePlanejadaDTO>> porGrupo = new HashMap<>();
        Map<Long, List<AtividadePlanejadaDTO>> semGrupoPorPratica = new HashMap<>();
        for (AtividadePlanejadaModel a : atividades.findByPlnCodOrderByOrdemAscAtpCodAsc(plano.getPlnCod())) {
            AtividadePlanejadaDTO dto = mapper.toDTO(a, rotuloResponsavel(a.getRespPesCod(), nomeResponsavel));
            if (a.getAgrcCod() != null) {
                porGrupo.computeIfAbsent(a.getAgrcCod(), k -> new ArrayList<>()).add(dto);
            } else {
                semGrupoPorPratica.computeIfAbsent(a.getPrtcCod(), k -> new ArrayList<>()).add(dto);
            }
        }

        List<PlanProcessoDTO> arvore = new ArrayList<>();
        for (ProcessoCicloModel proc : processosCiclo.findByCicCodOrderByOrdemAscPrccCodAsc(plano.getCicCod())) {
            List<PlanPraticaDTO> praticasDTO = new ArrayList<>();
            for (PraticaCicloModel pr : praticasCiclo.findByPrccCodOrderByOrdemAscPrtcCodAsc(proc.getPrccCod())) {
                // Grupos na ordem persistida (ORDEM); o sintético "Sem agrupamento" fica no fim.
                List<PlanGrupoDTO> grupos = new ArrayList<>();
                int maxOrdem = 0;
                for (AgrupamentoCicloModel g : agrupamentosCiclo.findByPrtcCodOrderByOrdemAscAgrcCodAsc(pr.getPrtcCod())) {
                    List<AtividadePlanejadaDTO> lista = porGrupo.getOrDefault(g.getAgrcCod(), List.of());
                    // Grupo de incubada vazio (resíduo de regeração com incubadas diferentes) não entra.
                    if (g.getEmpCod() != null && lista.isEmpty()) {
                        continue;
                    }
                    grupos.add(new PlanGrupoDTO(g.getAgrcCod(), g.getNome(), g.getOrdem(), g.getEmpCod(), lista));
                    maxOrdem = Math.max(maxOrdem, g.getOrdem());
                }
                // Atividades sem grupo (inclui complementares): grupo sintético no fim para não sumirem.
                List<AtividadePlanejadaDTO> semGrupo = semGrupoPorPratica.getOrDefault(pr.getPrtcCod(), List.of());
                if (!semGrupo.isEmpty()) {
                    grupos.add(new PlanGrupoDTO(null, "Sem agrupamento", maxOrdem + 1, null, semGrupo));
                }
                praticasDTO.add(mapper.toDTO(pr, grupos));
            }
            arvore.add(mapper.toDTO(proc, praticasDTO));
        }
        return arvore;
    }

    // ---- atividades ----

    /** Inclui uma atividade complementar na prática do ciclo (do planejamento vigente). */
    @Transactional(rollbackFor = Exception.class)
    public AtividadePlanejadaDTO adicionarComplementar(Long prtcCod, AtividadePlanejadaDTO dto) {
        PlanejamentoModel plano = exigirVigente();
        exigirPraticaCiclo(plano.getCicCod(), prtcCod);
        AtividadePlanejadaModel atv = mapper.toModel(dto);
        atv.setPlnCod(plano.getPlnCod());
        atv.setPrtcCod(prtcCod);
        atv.setAgrcCod(dto.agrcCod());  // grupo-alvo opcional; nulo cai em "Sem agrupamento"
        atv.setOrdem(proximaOrdemAtividade(plano.getPlnCod(), prtcCod));
        atv.setOrigem(EOrigemAtividade.COMPLEMENTAR);
        atv.setStatus(EStatusAtividade.PLANEJADA);
        atividades.save(atv);
        return mapper.toDTO(atv, rotuloResponsavel(atv.getRespPesCod()));
    }

    /** Ajusta nome/descrição/responsável/prazo de uma atividade (do modelo ou complementar). */
    @Transactional(rollbackFor = Exception.class)
    public AtividadePlanejadaDTO ajustarAtividade(Long atpCod, AtividadePlanejadaDTO dto) {
        PlanejamentoModel plano = exigirVigente();
        AtividadePlanejadaModel atv = buscarAtividade(plano.getPlnCod(), atpCod);
        mapper.atualizar(dto, atv);
        atividades.save(atv);
        return mapper.toDTO(atv, rotuloResponsavel(atv.getRespPesCod()));
    }

    /**
     * Exclui uma atividade do plano (do modelo ou complementar) — como na metodologia. Bloqueia se já
     * houver evidências vinculadas (a versão volta ao regerar o ciclo, se for do modelo).
     */
    @Transactional(rollbackFor = Exception.class)
    public void removerAtividade(Long atpCod) {
        PlanejamentoModel plano = exigirVigente();
        AtividadePlanejadaModel atv = buscarAtividade(plano.getPlnCod(), atpCod);
        if (evidencias.existsByAtpCod(atpCod)) {
            throw new RegraNegocioException(
                    "Esta atividade tem evidências registradas e não pode ser excluída.");
        }
        atividades.delete(atv);
    }

    /**
     * Reordena as atividades de uma prática do plano vigente conforme a sequência de {@code atpCods}
     * (arrastar-e-soltar): a posição na lista vira a nova {@code ordem}. A lista deve conter exatamente
     * as atividades da prática.
     */
    @Transactional(rollbackFor = Exception.class)
    public void reordenarAtividades(Long prtcCod, List<Long> atpCods) {
        PlanejamentoModel plano = exigirVigente();
        exigirPraticaCiclo(plano.getCicCod(), prtcCod);
        List<AtividadePlanejadaModel> todas = atividades.findByPlnCodAndPrtcCod(plano.getPlnCod(), prtcCod);
        Map<Long, AtividadePlanejadaModel> porId = new HashMap<>();
        for (AtividadePlanejadaModel a : todas) {
            porId.put(a.getAtpCod(), a);
        }
        if (atpCods == null || atpCods.size() != todas.size()) {
            throw new RegraNegocioException("A ordenação deve conter exatamente as atividades da prática.");
        }
        int ordem = 1;
        Set<Long> vistos = new HashSet<>();
        for (Long atpCod : atpCods) {
            AtividadePlanejadaModel a = porId.get(atpCod);
            if (a == null || !vistos.add(atpCod)) {
                throw new RegraNegocioException("A ordenação deve conter exatamente as atividades da prática.");
            }
            a.setOrdem(ordem++);
        }
        atividades.saveAll(todas);
    }

    /**
     * Reordena os agrupamentos (instância do ciclo) de uma prática conforme {@code agrcCods}. Os
     * informados assumem a ordem 1..n; os demais (ex.: grupos ocultos por não terem atividades no plano)
     * vão depois, na ordem atual. Reflete na estrutura do ciclo (compartilhada entre planos).
     */
    @Transactional(rollbackFor = Exception.class)
    public void reordenarAgrupamentos(Long prtcCod, List<Long> agrcCods) {
        PlanejamentoModel plano = exigirVigente();
        exigirPraticaCiclo(plano.getCicCod(), prtcCod);
        List<AgrupamentoCicloModel> todos = agrupamentosCiclo.findByPrtcCodOrderByOrdemAscAgrcCodAsc(prtcCod);
        Map<Long, AgrupamentoCicloModel> porId = new HashMap<>();
        for (AgrupamentoCicloModel g : todos) {
            porId.put(g.getAgrcCod(), g);
        }
        int ordem = 1;
        Set<Long> vistos = new HashSet<>();
        for (Long agrcCod : agrcCods == null ? List.<Long>of() : agrcCods) {
            AgrupamentoCicloModel g = porId.get(agrcCod);
            if (g == null || !vistos.add(agrcCod)) {
                throw new RegraNegocioException("A ordenação deve conter agrupamentos desta prática.");
            }
            g.setOrdem(ordem++);
        }
        for (AgrupamentoCicloModel g : todos) {
            if (!vistos.contains(g.getAgrcCod())) {
                g.setOrdem(ordem++);
            }
        }
        agrupamentosCiclo.saveAll(todos);
    }

    // ---- apoio ----

    private java.util.Optional<PlanejamentoModel> planejamentoVigente(Long cicCod) {
        return planejamentos.findByCicCodAndStatus(cicCod, EStatusPlanejamento.PUBLICADO);
    }

    /** Planejamento vigente do ciclo ativo, ou {@code null} (sem ciclo/sem plano). */
    private PlanejamentoModel vigenteDoCicloAtivo() {
        CiclosModel ciclo = cicloContexto.emFoco();
        return ciclo == null ? null : planejamentoVigente(ciclo.getCicCod()).orElse(null);
    }

    private PlanejamentoModel exigirVigente() {
        PlanejamentoModel plano = vigenteDoCicloAtivo();
        if (plano == null) {
            throw new RegraNegocioException("Não há planejamento vigente no ciclo ativo.");
        }
        return plano;
    }

    /** Busca a atividade garantindo que pertence ao planejamento informado. */
    private AtividadePlanejadaModel buscarAtividade(Long plnCod, Long atpCod) {
        AtividadePlanejadaModel atv = atividades.findById(atpCod)
                .orElseThrow(() -> new NaoEncontradoException("Atividade", atpCod));
        if (!atv.getPlnCod().equals(plnCod)) {
            throw new RegraNegocioException("A atividade não pertence a este planejamento.");
        }
        return atv;
    }

    /** Próxima {@code ordem} da prática no plano (anexa a complementar no fim). */
    private int proximaOrdemAtividade(Long plnCod, Long prtcCod) {
        AtividadePlanejadaModel ultima = atividades
                .findFirstByPlnCodAndPrtcCodOrderByOrdemDesc(plnCod, prtcCod).orElse(null);
        return ultima == null ? 1 : ultima.getOrdem() + 1;
    }

    /** Garante que a prática (instância) existe e pertence ao ciclo informado. */
    private void exigirPraticaCiclo(Long cicCod, Long prtcCod) {
        PraticaCicloModel pratica = praticasCiclo.findById(prtcCod).orElse(null);
        if (pratica == null || !pratica.getCicCod().equals(cicCod)) {
            throw new NaoEncontradoException("Prática", prtcCod);
        }
    }

    /** Monta o DTO do cabeçalho com rótulos e o resumo de execução (total/concluídas/progresso). */
    private PlanejamentoDTO toDTO(PlanejamentoModel plano) {
        int total = (int) atividades.countByPlnCod(plano.getPlnCod());
        int concluidas = (int) atividades.countByPlnCodAndStatus(
                plano.getPlnCod(), EStatusAtividade.CONCLUIDA);
        int progresso = total == 0 ? 0 : Math.round(concluidas * 100f / total);
        return mapper.toDTO(plano,
                rotuloCiclo(plano.getCicCod()),
                rotuloResponsavel(plano.getRespPesCod()),
                total, concluidas, progresso);
    }

    private String rotuloCiclo(Long cicCod) {
        return ciclos.findById(cicCod).map(CiclosModel::getNome).orElse(null);
    }

    /** Pessoa ({@code PES_COD}) do usuário logado neste tenant, ou {@code null} se não resolvível. */
    private Long pessoaAtual() {
        Long ctaCod = SessaoContext.contaAtual();
        if (ctaCod == null) {
            return null;
        }
        return pessoas.findByCtaCod(ctaCod).map(PessoasModel::getPesCod).orElse(null);
    }

    /** Nome do responsável (PESSOAS → public.CONTAS); {@code null} se não definido ou não resolvível. */
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

    /** Como {@link #rotuloResponsavel(Long)}, memorizando por {@code PES_COD} (evita relê-lo por atividade). */
    private String rotuloResponsavel(Long pesCod, Map<Long, String> cache) {
        if (pesCod == null) {
            return null;
        }
        if (cache.containsKey(pesCod)) {
            return cache.get(pesCod);
        }
        String nome = rotuloResponsavel(pesCod);
        cache.put(pesCod, nome);
        return nome;
    }
}

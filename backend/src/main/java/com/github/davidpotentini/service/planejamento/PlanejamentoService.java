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
import com.github.davidpotentini.repository.ciclos.CicloEmpreendimentoRepository;
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
 * Planejamento institucional do ciclo. No máx. um vigente ({@code PUBLICADO}) por ciclo; "Gerar do
 * ciclo" substitui o anterior (que vira {@code ENCERRADO}, como histórico).
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
    private final CicloEmpreendimentoRepository cicloEmpreendimentos;
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
                               AgrupamentoCicloRepository agrupamentosCiclo,
                               CicloEmpreendimentoRepository cicloEmpreendimentos,
                               EvidenciaRepository evidencias,
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
        this.cicloEmpreendimentos = cicloEmpreendimentos;
        this.evidencias = evidencias;
        this.pessoas = pessoas;
        this.contas = contas;
        this.mapper = mapper;
    }


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
     * Consumidor da estrutura (find-only, já materializada por {@code EstruturaCicloService}): copia as
     * atividades ATIVAS da metodologia. As marcadas "da incubada" duplicam por empreendimento de
     * {@code empCods}, cada cópia num grupo dinâmico da incubada.
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

    @Transactional(rollbackFor = Exception.class)
    public PlanejamentoDTO gerarAtividadesEmpreendimento(Long empCod) {
        CiclosModel ciclo = cicloContexto.emFoco();
        if (ciclo == null) {
            throw new RegraNegocioException("Não há ciclo ativo.");
        }
        PlanejamentoModel plano = planejamentoVigente(ciclo.getCicCod())
                .orElseThrow(() -> new RegraNegocioException(
                        "Publique a metodologia para o ciclo antes de gerar as atividades por empreendimento."));
        if (!cicloEmpreendimentos.existsByCicCodAndEmpCod(ciclo.getCicCod(), empCod)) {
            throw new RegraNegocioException("O empreendimento não está vinculado a este ciclo.");
        }
        if (atividades.existsByPlnCodAndEmpCod(plano.getPlnCod(), empCod)) {
            throw new RegraNegocioException(
                    "Este empreendimento já tem atividades por empreendimento neste ciclo.");
        }
        EmpreendimentosModel emp = empreendimentos.findById(empCod)
                .orElseThrow(() -> new NaoEncontradoException("Empreendimento", empCod));

        Map<Long, Long> prtcPorPrt = new HashMap<>();
        for (PraticaCicloModel pr : praticasCiclo.findByCicCodOrderByPrtcCodAsc(ciclo.getCicCod())) {
            if (pr.getPrtCodOrigem() != null) {
                prtcPorPrt.put(pr.getPrtCodOrigem(), pr.getPrtcCod());
            }
        }

        int ordemGrupo = 1000;
        for (AgrupamentoCicloModel g : agrupamentosCiclo.findByCicCodOrderByAgrcCodAsc(ciclo.getCicCod())) {
            if (g.getEmpCod() != null && g.getOrdem() >= ordemGrupo) {
                ordemGrupo = g.getOrdem() + 1;
            }
        }

        for (AtividadeMetodologiaModel base
                : atividadesMetodologia.findByPrtCodInOrderByOrdemAscNomeAsc(prtcPorPrt.keySet())) {
            if (base.getSituacao() != EAtivoInativo.ATIVO || !base.isPorEmpreendimento()) {
                continue;
            }
            Long prtcCod = prtcPorPrt.get(base.getPrtCod());
            Long agrcCod = garantirGrupoEmpreendimento(ciclo.getCicCod(), prtcCod, empCod,
                    emp.getNome(), ordemGrupo);
            salvarCopiaAtividade(plano, base, prtcCod, agrcCod, empCod);
        }
        return toDTO(plano);
    }

    /** Grupo dinâmico da incubada (find-or-create por {@code (cicCod, prtcCod, empCod)}); sem origem no template. */
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

    /** Trava do "Gerar do ciclo": conta como edição um complementar, responsável/prazo definidos, execução iniciada ou evidência. */
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

    @Transactional(readOnly = true)
    public boolean empreendimentoTemEdicoes(Long cicCod, Long empCod) {
        PlanejamentoModel plano = planejamentoVigente(cicCod).orElse(null);
        if (plano == null) {
            return false;
        }
        Long plnCod = plano.getPlnCod();
        return atividades.existsByPlnCodAndEmpCodAndOrigem(plnCod, empCod, EOrigemAtividade.COMPLEMENTAR)
                || atividades.existsByPlnCodAndEmpCodAndRespPesCodNotNull(plnCod, empCod)
                || atividades.existsByPlnCodAndEmpCodAndPrazoNotNull(plnCod, empCod)
                || atividades.existsByPlnCodAndEmpCodAndStatusNot(plnCod, empCod, EStatusAtividade.PLANEJADA);
    }


    @Transactional(readOnly = true)
    public List<PlanProcessoDTO> estrutura() {
        PlanejamentoModel plano = vigenteDoCicloAtivo();
        if (plano == null) {
            return List.of();
        }

        // Atividades por agrupamento (AGRC_COD); as sem grupo ficam por prática, para o grupo sintético.
        Map<Long, List<AtividadePlanejadaDTO>> porGrupo = new HashMap<>();
        Map<Long, List<AtividadePlanejadaDTO>> semGrupoPorPratica = new HashMap<>();
        for (AtividadePlanejadaModel a : atividades.findByPlnCodOrderByOrdemAscAtpCodAsc(plano.getPlnCod())) {
            AtividadePlanejadaDTO dto = mapper.toDTO(a, rotuloEmpreendimento(a.getEmpCod()), rotuloResponsavel(a.getRespPesCod()));
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
        return mapper.toDTO(atv, rotuloEmpreendimento(atv.getEmpCod()), rotuloResponsavel(atv.getRespPesCod()));
    }

    @Transactional(rollbackFor = Exception.class)
    public AtividadePlanejadaDTO ajustarAtividade(Long atpCod, AtividadePlanejadaDTO dto) {
        PlanejamentoModel plano = exigirVigente();
        AtividadePlanejadaModel atv = buscarAtividade(plano.getPlnCod(), atpCod);
        mapper.atualizar(dto, atv);
        atividades.save(atv);
        return mapper.toDTO(atv, rotuloEmpreendimento(atv.getEmpCod()), rotuloResponsavel(atv.getRespPesCod()));
    }

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

    /** Os informados assumem 1..n; os demais (ex.: grupos ocultos) vão depois. Reflete na estrutura do ciclo (compartilhada entre planos). */
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

    @Transactional(rollbackFor = Exception.class)
    public void excluirAgrupamento(Long agrcCod) {
        PlanejamentoModel plano = exigirVigente();
        AgrupamentoCicloModel grupo = agrupamentosCiclo.findById(agrcCod)
                .orElseThrow(() -> new NaoEncontradoException("Agrupamento", agrcCod));
        exigirPraticaCiclo(plano.getCicCod(), grupo.getPrtcCod());
        if (grupo.getEmpCod() != null) {
            throw new RegraNegocioException(
                    "Grupos por empreendimento são regerados ao gerar o ciclo e não podem ser excluídos.");
        }
        if (atividades.existsByAgrcCod(agrcCod)) {
            throw new RegraNegocioException(
                    "Este agrupamento tem atividades e não pode ser excluído. "
                    + "Mova ou exclua as atividades antes.");
        }
        agrupamentosCiclo.delete(grupo);
    }


    private java.util.Optional<PlanejamentoModel> planejamentoVigente(Long cicCod) {
        return planejamentos.findByCicCodAndStatus(cicCod, EStatusPlanejamento.PUBLICADO);
    }

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

    private AtividadePlanejadaModel buscarAtividade(Long plnCod, Long atpCod) {
        AtividadePlanejadaModel atv = atividades.findById(atpCod)
                .orElseThrow(() -> new NaoEncontradoException("Atividade", atpCod));
        if (!atv.getPlnCod().equals(plnCod)) {
            throw new RegraNegocioException("A atividade não pertence a este planejamento.");
        }
        return atv;
    }

    private int proximaOrdemAtividade(Long plnCod, Long prtcCod) {
        AtividadePlanejadaModel ultima = atividades
                .findFirstByPlnCodAndPrtcCodOrderByOrdemDesc(plnCod, prtcCod).orElse(null);
        return ultima == null ? 1 : ultima.getOrdem() + 1;
    }

    private void exigirPraticaCiclo(Long cicCod, Long prtcCod) {
        PraticaCicloModel pratica = praticasCiclo.findById(prtcCod).orElse(null);
        if (pratica == null || !pratica.getCicCod().equals(cicCod)) {
            throw new NaoEncontradoException("Prática", prtcCod);
        }
    }

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

    private Long pessoaAtual() {
        Long ctaCod = SessaoContext.contaAtual();
        if (ctaCod == null) {
            return null;
        }
        return pessoas.findByCtaCod(ctaCod).map(PessoasModel::getPesCod).orElse(null);
    }

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

    private String rotuloEmpreendimento(Long empCod) {
        if (empCod == null) {
            return null;
        }
        EmpreendimentosModel empreendimento = empreendimentos.findById(empCod).orElse(null);
        if (empreendimento == null) {
            return null;
        }
        return empreendimento.getNome();
    }

}

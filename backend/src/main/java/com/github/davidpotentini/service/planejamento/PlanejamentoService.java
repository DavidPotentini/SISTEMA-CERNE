package com.github.davidpotentini.service.planejamento;

import com.github.davidpotentini.comum.erro.NaoEncontradoException;
import com.github.davidpotentini.comum.erro.RegraNegocioException;
import com.github.davidpotentini.comum.tenant.SessaoContext;
import com.github.davidpotentini.dto.planejamento.AtividadePlanejadaDTO;
import com.github.davidpotentini.dto.planejamento.PlanPraticaDTO;
import com.github.davidpotentini.dto.planejamento.PlanProcessoDTO;
import com.github.davidpotentini.dto.planejamento.PlanejamentoAtualDTO;
import com.github.davidpotentini.dto.planejamento.PlanejamentoDTO;
import com.github.davidpotentini.enums.EAtivoInativo;
import com.github.davidpotentini.enums.EOrigemAtividade;
import com.github.davidpotentini.enums.EStatusAtividade;
import com.github.davidpotentini.enums.EStatusCiclo;
import com.github.davidpotentini.enums.EStatusModelo;
import com.github.davidpotentini.enums.EStatusPlanejamento;
import com.github.davidpotentini.mapper.planejamento.PlanejamentoMapper;
import com.github.davidpotentini.model.ciclos.CiclosModel;
import com.github.davidpotentini.model.contas.ContasModel;
import com.github.davidpotentini.model.metodologia.PraticaModel;
import com.github.davidpotentini.model.metodologia.ProcessoModel;
import com.github.davidpotentini.model.modelos.AtividadeModeloModel;
import com.github.davidpotentini.model.modelos.ModeloModel;
import com.github.davidpotentini.model.pessoas.PessoasModel;
import com.github.davidpotentini.model.planejamento.AtividadePlanejadaModel;
import com.github.davidpotentini.model.planejamento.PlanejamentoModel;
import com.github.davidpotentini.repository.ciclos.CiclosRepository;
import com.github.davidpotentini.repository.contas.ContasRepository;
import com.github.davidpotentini.repository.metodologia.PraticaRepository;
import com.github.davidpotentini.repository.metodologia.ProcessoRepository;
import com.github.davidpotentini.repository.modelos.AtividadeModeloRepository;
import com.github.davidpotentini.repository.modelos.ModeloRepository;
import com.github.davidpotentini.repository.pessoas.PessoasRepository;
import com.github.davidpotentini.repository.planejamento.AtividadePlanejadaRepository;
import com.github.davidpotentini.repository.planejamento.PlanejamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Planejamento institucional do ciclo ativo (schema do tenant vem do JWT).
 *
 * <p>Há no máx. um planejamento vigente ({@code PUBLICADO}) por ciclo. "Gerar de modelo" cria um
 * planejamento para o ciclo ativo copiando as atividades ATIVAS de um modelo publicado; se já houver
 * um vigente, ele é <b>substituído</b> — o anterior é inativado ({@code ENCERRADO}) e fica como
 * histórico. A estrutura de processos/práticas é herdada da metodologia base do modelo (não copiada);
 * as atividades copiadas podem ser ajustadas e complementares podem ser incluídas.
 */
@Service
public class PlanejamentoService {

    private final PlanejamentoRepository planejamentos;
    private final AtividadePlanejadaRepository atividades;
    private final CiclosRepository ciclos;
    private final ModeloRepository modelos;
    private final AtividadeModeloRepository atividadesModelo;
    private final ProcessoRepository processos;
    private final PraticaRepository praticas;
    private final PessoasRepository pessoas;
    private final ContasRepository contas;
    private final PlanejamentoMapper mapper;

    public PlanejamentoService(PlanejamentoRepository planejamentos,
                               AtividadePlanejadaRepository atividades, CiclosRepository ciclos,
                               ModeloRepository modelos, AtividadeModeloRepository atividadesModelo,
                               ProcessoRepository processos, PraticaRepository praticas,
                               PessoasRepository pessoas, ContasRepository contas,
                               PlanejamentoMapper mapper) {
        this.planejamentos = planejamentos;
        this.atividades = atividades;
        this.ciclos = ciclos;
        this.modelos = modelos;
        this.atividadesModelo = atividadesModelo;
        this.processos = processos;
        this.praticas = praticas;
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
        CiclosModel ciclo = cicloAtivo();
        if (ciclo == null) {
            return new PlanejamentoAtualDTO(false, null, null, null);
        }
        PlanejamentoDTO dto = planejamentoVigente(ciclo.getCicCod())
                .map(this::toDTO)
                .orElse(null);
        return new PlanejamentoAtualDTO(true, ciclo.getCicCod(), ciclo.getNome(), dto);
    }

    /**
     * Gera o planejamento do ciclo ativo a partir de um modelo publicado. Substitui o vigente, se
     * houver (o anterior vira {@code ENCERRADO}). O responsável do plano é o usuário logado. Copia as
     * atividades ATIVAS do modelo com os mesmos valores ({@code prtCod}/{@code nome}/{@code observacoes}/
     * {@code respPesCod}).
     */
    @Transactional(rollbackFor = Exception.class)
    public PlanejamentoDTO gerarDeModelo(Long modCod) {
        CiclosModel ciclo = cicloAtivo();
        if (ciclo == null) {
            throw new RegraNegocioException("Não há ciclo ativo. Abra um ciclo antes de planejar.");
        }
        ModeloModel modelo = modelos.findById(modCod)
                .orElseThrow(() -> new NaoEncontradoException("Modelo", modCod));
        if (modelo.getStatus() != EStatusModelo.PUBLICADO) {
            throw new RegraNegocioException("Só é possível gerar a partir de um modelo publicado.");
        }

        // Substitui o vigente: o anterior é inativado (ENCERRADO), preservado como histórico.
        planejamentoVigente(ciclo.getCicCod()).ifPresent(anterior -> {
            anterior.setStatus(EStatusPlanejamento.ENCERRADO);
            planejamentos.saveAndFlush(anterior);
        });

        PlanejamentoModel plano = new PlanejamentoModel();
        plano.setNome("Planejamento — " + ciclo.getNome());
        plano.setCicCod(ciclo.getCicCod());
        plano.setModCod(modelo.getModCod());
        plano.setStatus(EStatusPlanejamento.PUBLICADO);
        plano.setRespPesCod(pessoaAtual());
        plano.setInicio(ciclo.getInicio());
        plano.setFim(ciclo.getFim());
        planejamentos.save(plano);

        for (AtividadeModeloModel base : atividadesModelo.findByModCodOrderByAtmCodAsc(modCod)) {
            if (base.getSituacao() != EAtivoInativo.ATIVO) {
                continue;
            }
            AtividadePlanejadaModel atv = new AtividadePlanejadaModel();
            atv.setPlnCod(plano.getPlnCod());
            atv.setOrigem(EOrigemAtividade.MODELO);
            atv.setPrtCod(base.getPrtCod());
            atv.setNome(base.getNome());
            atv.setObservacoes(base.getObservacoes());
            atv.setRespPesCod(base.getRespPesCod());
            atv.setStatus(EStatusAtividade.PLANEJADA);
            atividades.save(atv);
        }
        return toDTO(plano);
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
        Map<Long, List<AtividadePlanejadaDTO>> porPratica = new HashMap<>();
        for (AtividadePlanejadaModel a : atividades.findByPlnCodOrderByAtpCodAsc(plano.getPlnCod())) {
            porPratica.computeIfAbsent(a.getPrtCod(), k -> new ArrayList<>())
                    .add(mapper.toDTO(a, rotuloResponsavel(a.getRespPesCod(), nomeResponsavel)));
        }

        List<PlanProcessoDTO> arvore = new ArrayList<>();
        for (ProcessoModel proc : processos.findAllByOrderByOrdemAscPrcCodAsc()) {
            if (proc.getSituacao() != EAtivoInativo.ATIVO) {
                continue;
            }
            List<PlanPraticaDTO> praticasDTO = new ArrayList<>();
            for (PraticaModel pr : praticas.findByPrcCodOrderByPrtCodAsc(proc.getPrcCod())) {
                if (pr.getSituacao() != EAtivoInativo.ATIVO) {
                    continue;
                }
                praticasDTO.add(mapper.toDTO(pr, porPratica.getOrDefault(pr.getPrtCod(), List.of())));
            }
            arvore.add(mapper.toDTO(proc, praticasDTO));
        }
        return arvore;
    }

    // ---- atividades ----

    /** Inclui uma atividade complementar na prática (do planejamento vigente). */
    @Transactional(rollbackFor = Exception.class)
    public AtividadePlanejadaDTO adicionarComplementar(Long prtCod, AtividadePlanejadaDTO dto) {
        PlanejamentoModel plano = exigirVigente();
        exigirPraticaExiste(prtCod);
        AtividadePlanejadaModel atv = mapper.toModel(dto);
        atv.setPlnCod(plano.getPlnCod());
        atv.setPrtCod(prtCod);
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

    /** Remove uma atividade complementar (as do modelo não são removidas, só ajustadas). */
    @Transactional(rollbackFor = Exception.class)
    public void removerComplementar(Long atpCod) {
        PlanejamentoModel plano = exigirVigente();
        AtividadePlanejadaModel atv = buscarAtividade(plano.getPlnCod(), atpCod);
        if (atv.getOrigem() != EOrigemAtividade.COMPLEMENTAR) {
            throw new RegraNegocioException("Só atividades complementares podem ser removidas.");
        }
        atividades.delete(atv);
    }

    // ---- apoio ----

    private CiclosModel cicloAtivo() {
        List<CiclosModel> ativos = ciclos.findByStatus(EStatusCiclo.ATIVO);
        return ativos.isEmpty() ? null : ativos.get(0);
    }

    private java.util.Optional<PlanejamentoModel> planejamentoVigente(Long cicCod) {
        return planejamentos.findByCicCodAndStatus(cicCod, EStatusPlanejamento.PUBLICADO);
    }

    /** Planejamento vigente do ciclo ativo, ou {@code null} (sem ciclo/sem plano). */
    private PlanejamentoModel vigenteDoCicloAtivo() {
        CiclosModel ciclo = cicloAtivo();
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

    /** Garante que a prática existe na metodologia. */
    private void exigirPraticaExiste(Long prtCod) {
        if (!praticas.existsById(prtCod)) {
            throw new NaoEncontradoException("Prática", prtCod);
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
                rotuloModelo(plano.getModCod()),
                rotuloResponsavel(plano.getRespPesCod()),
                total, concluidas, progresso);
    }

    private String rotuloCiclo(Long cicCod) {
        return ciclos.findById(cicCod).map(CiclosModel::getNome).orElse(null);
    }

    private String rotuloModelo(Long modCod) {
        return modCod == null ? null
                : modelos.findById(modCod).map(ModeloModel::getNome).orElse(null);
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

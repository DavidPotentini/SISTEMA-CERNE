package com.github.davidpotentini.service.modelos;

import com.github.davidpotentini.comum.erro.NaoEncontradoException;
import com.github.davidpotentini.comum.erro.RegraNegocioException;
import com.github.davidpotentini.dto.modelos.AtividadeModeloDTO;
import com.github.davidpotentini.dto.modelos.ModeloDTO;
import com.github.davidpotentini.dto.modelos.ModeloPraticaDTO;
import com.github.davidpotentini.dto.modelos.ModeloProcessoDTO;
import com.github.davidpotentini.enums.EAtivoInativo;
import com.github.davidpotentini.enums.ESituacaoVersao;
import com.github.davidpotentini.enums.EStatusModelo;
import com.github.davidpotentini.mapper.modelos.ModeloMapper;
import com.github.davidpotentini.model.metodologia.PraticaModel;
import com.github.davidpotentini.model.metodologia.ProcessoModel;
import com.github.davidpotentini.model.metodologia.VersaoMetodologiaModel;
import com.github.davidpotentini.model.modelos.AtividadeModeloModel;
import com.github.davidpotentini.model.modelos.ModeloModel;
import com.github.davidpotentini.repository.metodologia.PraticaRepository;
import com.github.davidpotentini.repository.metodologia.ProcessoRepository;
import com.github.davidpotentini.repository.metodologia.VersaoMetodologiaRepository;
import com.github.davidpotentini.repository.modelos.AtividadeModeloRepository;
import com.github.davidpotentini.repository.modelos.ModeloRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Modelos de planejamento da incubadora logada (schema do tenant vem do JWT).
 *
 * <p>Um modelo referencia a metodologia VIGENTE no momento da criação e herda dela a estrutura de
 * processos/práticas ATIVOS (não copia — só lê pela cadeia {@code versão → processos → práticas}). O
 * que o modelo guarda são as atividades de cada prática. Nesta tela processos/práticas não são
 * editáveis; só as atividades. Modelo {@code PUBLICADO} é imutável.
 */
@Service
public class ModeloService {

    private final ModeloRepository modelos;
    private final AtividadeModeloRepository atividades;
    private final VersaoMetodologiaRepository versoes;
    private final ProcessoRepository processos;
    private final PraticaRepository praticas;
    private final ModeloMapper mapper;

    public ModeloService(ModeloRepository modelos, AtividadeModeloRepository atividades,
                         VersaoMetodologiaRepository versoes, ProcessoRepository processos,
                         PraticaRepository praticas, ModeloMapper mapper) {
        this.modelos = modelos;
        this.atividades = atividades;
        this.versoes = versoes;
        this.processos = processos;
        this.praticas = praticas;
        this.mapper = mapper;
    }

    // ---- modelo (cabeçalho) ----

    /** Modelos da incubadora, mais recentes primeiro. */
    @Transactional(readOnly = true)
    public List<ModeloDTO> listarModelos() {
        List<ModeloDTO> lista = new ArrayList<>();
        for (ModeloModel m : modelos.findAllByOrderByModCodDesc()) {
            lista.add(mapper.toDTO(m, rotuloVersao(m.getVerCod())));
        }
        return lista;
    }

    @Transactional(readOnly = true)
    public ModeloDTO obterModelo(Long modCod) {
        ModeloModel modelo = buscarModelo(modCod);
        return mapper.toDTO(modelo, rotuloVersao(modelo.getVerCod()));
    }

    /** Novo modelo baseado na última metodologia VIGENTE (resolvida aqui); nasce RASCUNHO. */
    @Transactional(rollbackFor = Exception.class)
    public ModeloDTO criarModelo(ModeloDTO dto) {
        VersaoMetodologiaModel vigente = versoes
                .findFirstBySituacaoOrderByVerCodDesc(ESituacaoVersao.VIGENTE)
                .orElseThrow(() -> new RegraNegocioException(
                        "Publique uma versão da metodologia antes de criar um modelo."));
        ModeloModel modelo = mapper.toModel(dto);
        modelo.setVerCod(vigente.getVerCod());
        modelo.setStatus(EStatusModelo.RASCUNHO);
        modelos.save(modelo);
        return mapper.toDTO(modelo, vigente.getVersao());
    }

    /** Edita o cabeçalho — só enquanto RASCUNHO. */
    @Transactional(rollbackFor = Exception.class)
    public ModeloDTO editarModelo(Long modCod, ModeloDTO dto) {
        ModeloModel modelo = buscarModelo(modCod);
        exigirRascunho(modelo);
        mapper.atualizar(dto, modelo);
        modelos.save(modelo);
        return mapper.toDTO(modelo, rotuloVersao(modelo.getVerCod()));
    }

    /** Publica o modelo (imutável a partir daqui); carimba a data de publicação. */
    @Transactional(rollbackFor = Exception.class)
    public ModeloDTO publicarModelo(Long modCod) {
        ModeloModel modelo = buscarModelo(modCod);
        if (modelo.getStatus() == EStatusModelo.PUBLICADO) {
            throw new RegraNegocioException("Este modelo já está publicado.");
        }
        modelo.setStatus(EStatusModelo.PUBLICADO);
        modelo.setPublicadoEm(LocalDateTime.now());
        modelos.save(modelo);
        return mapper.toDTO(modelo, rotuloVersao(modelo.getVerCod()));
    }

    // ---- estrutura (processos/práticas herdados + atividades) ----

    /**
     * Estrutura do modelo: processos e práticas ATIVOS da versão base (só leitura), cada prática com
     * suas atividades (inclusive inativas — continuam visíveis, atenuadas no front).
     */
    @Transactional(readOnly = true)
    public List<ModeloProcessoDTO> estruturaModelo(Long modCod) {
        ModeloModel modelo = buscarModelo(modCod);

        Map<Long, List<AtividadeModeloDTO>> porPratica = new HashMap<>();
        for (AtividadeModeloModel a : atividades.findByModCodOrderByAtmCodAsc(modCod)) {
            porPratica.computeIfAbsent(a.getPrtCod(), k -> new ArrayList<>()).add(mapper.toDTO(a));
        }

        List<ModeloProcessoDTO> arvore = new ArrayList<>();
        for (ProcessoModel proc : processos.findByVerCodOrderByOrdemAscPrcCodAsc(modelo.getVerCod())) {
            if (proc.getSituacao() != EAtivoInativo.ATIVO) {
                continue;
            }
            List<ModeloPraticaDTO> praticasDTO = new ArrayList<>();
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

    /** Adiciona uma atividade à prática (só no RASCUNHO). */
    @Transactional(rollbackFor = Exception.class)
    public AtividadeModeloDTO adicionarAtividade(Long modCod, Long prtCod, AtividadeModeloDTO dto) {
        ModeloModel modelo = buscarModelo(modCod);
        exigirRascunho(modelo);
        exigirPraticaNaVersao(modelo.getVerCod(), prtCod);
        AtividadeModeloModel atividade = mapper.toModel(dto);
        atividade.setModCod(modCod);
        atividade.setPrtCod(prtCod);
        atividade.setSituacao(EAtivoInativo.ATIVO);
        atividades.save(atividade);
        return mapper.toDTO(atividade);
    }

    /** Edita nome/descrição da atividade (só no RASCUNHO). */
    @Transactional(rollbackFor = Exception.class)
    public AtividadeModeloDTO editarAtividade(Long modCod, Long atmCod, AtividadeModeloDTO dto) {
        ModeloModel modelo = buscarModelo(modCod);
        exigirRascunho(modelo);
        AtividadeModeloModel atividade = buscarAtividade(modCod, atmCod);
        mapper.atualizar(dto, atividade);
        atividades.save(atividade);
        return mapper.toDTO(atividade);
    }

    /** Ativa/inativa a atividade (só no RASCUNHO). Inativa continua visível, mas atenuada. */
    @Transactional(rollbackFor = Exception.class)
    public AtividadeModeloDTO alterarSituacaoAtividade(Long modCod, Long atmCod, EAtivoInativo situacao) {
        ModeloModel modelo = buscarModelo(modCod);
        exigirRascunho(modelo);
        AtividadeModeloModel atividade = buscarAtividade(modCod, atmCod);
        atividade.setSituacao(situacao);
        atividades.save(atividade);
        return mapper.toDTO(atividade);
    }

    // ---- apoio ----

    private ModeloModel buscarModelo(Long modCod) {
        return modelos.findById(modCod)
                .orElseThrow(() -> new NaoEncontradoException("Modelo", modCod));
    }

    /** Busca a atividade garantindo que pertence ao modelo informado. */
    private AtividadeModeloModel buscarAtividade(Long modCod, Long atmCod) {
        AtividadeModeloModel atividade = atividades.findById(atmCod)
                .orElseThrow(() -> new NaoEncontradoException("Atividade", atmCod));
        if (!atividade.getModCod().equals(modCod)) {
            throw new RegraNegocioException("A atividade não pertence a este modelo.");
        }
        return atividade;
    }

    private void exigirRascunho(ModeloModel modelo) {
        if (modelo.getStatus() == EStatusModelo.PUBLICADO) {
            throw new RegraNegocioException("Modelo publicado não pode ser editado.");
        }
    }

    /** Garante que a prática pertence à metodologia base do modelo (cadeia prática → processo → versão). */
    private void exigirPraticaNaVersao(Long verCod, Long prtCod) {
        PraticaModel pratica = praticas.findById(prtCod)
                .orElseThrow(() -> new NaoEncontradoException("Prática", prtCod));
        ProcessoModel processo = processos.findById(pratica.getPrcCod())
                .orElseThrow(() -> new NaoEncontradoException("Processo", pratica.getPrcCod()));
        if (!processo.getVerCod().equals(verCod)) {
            throw new RegraNegocioException("A prática não pertence à metodologia deste modelo.");
        }
    }

    private String rotuloVersao(Long verCod) {
        return versoes.findById(verCod).map(VersaoMetodologiaModel::getVersao).orElse(null);
    }
}

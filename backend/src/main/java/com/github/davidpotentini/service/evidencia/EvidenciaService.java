package com.github.davidpotentini.service.evidencia;

import com.github.davidpotentini.comum.erro.NaoEncontradoException;
import com.github.davidpotentini.comum.erro.RegraNegocioException;
import com.github.davidpotentini.comum.tenant.SessaoContext;
import com.github.davidpotentini.dto.evidencia.AtividadeOpcaoDTO;
import com.github.davidpotentini.dto.evidencia.EvidenciaDTO;
import com.github.davidpotentini.enums.EStatusAtividade;
import com.github.davidpotentini.enums.EStatusEvidencia;
import com.github.davidpotentini.enums.EStatusPlanejamento;
import com.github.davidpotentini.mapper.evidencia.EvidenciaMapper;
import com.github.davidpotentini.model.arquivo.ArquivoModel;
import com.github.davidpotentini.comum.ciclo.CicloContexto;
import com.github.davidpotentini.model.ciclos.CiclosModel;
import com.github.davidpotentini.model.contas.ContasModel;
import com.github.davidpotentini.model.estruturaciclo.PraticaCicloModel;
import com.github.davidpotentini.model.estruturaciclo.ProcessoCicloModel;
import com.github.davidpotentini.model.evidencia.EvidenciaModel;
import com.github.davidpotentini.model.pessoas.PessoasModel;
import com.github.davidpotentini.model.planejamento.AtividadePlanejadaModel;
import com.github.davidpotentini.model.planejamento.PlanejamentoModel;
import com.github.davidpotentini.repository.arquivo.ArquivoRepository;
import com.github.davidpotentini.repository.contas.ContasRepository;
import com.github.davidpotentini.repository.estruturaciclo.PraticaCicloRepository;
import com.github.davidpotentini.repository.estruturaciclo.ProcessoCicloRepository;
import com.github.davidpotentini.repository.evidencia.EvidenciaRepository;
import com.github.davidpotentini.repository.pessoas.PessoasRepository;
import com.github.davidpotentini.repository.planejamento.AtividadePlanejadaRepository;
import com.github.davidpotentini.repository.planejamento.PlanejamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EvidenciaService {

    private final EvidenciaRepository evidencias;
    private final AtividadePlanejadaRepository atividades;
    private final ArquivoRepository arquivos;
    private final CicloContexto cicloContexto;
    private final PlanejamentoRepository planejamentos;
    private final ProcessoCicloRepository processosCiclo;
    private final PraticaCicloRepository praticasCiclo;
    private final PessoasRepository pessoas;
    private final ContasRepository contas;
    private final EvidenciaMapper mapper;

    public EvidenciaService(EvidenciaRepository evidencias,
                            AtividadePlanejadaRepository atividades, ArquivoRepository arquivos,
                            CicloContexto cicloContexto, PlanejamentoRepository planejamentos,
                            ProcessoCicloRepository processosCiclo, PraticaCicloRepository praticasCiclo,
                            PessoasRepository pessoas, ContasRepository contas, EvidenciaMapper mapper) {
        this.evidencias = evidencias;
        this.atividades = atividades;
        this.arquivos = arquivos;
        this.cicloContexto = cicloContexto;
        this.planejamentos = planejamentos;
        this.processosCiclo = processosCiclo;
        this.praticasCiclo = praticasCiclo;
        this.pessoas = pessoas;
        this.contas = contas;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<EvidenciaDTO> listar() {
        List<EvidenciaDTO> lista = new ArrayList<>();
        for (EvidenciaModel e : evidencias.versoesCorrentes()) {
            lista.add(toDTO(e));
        }
        return lista;
    }

    @Transactional(readOnly = true)
    public List<EvidenciaDTO> historico(Long evdCod) {
        List<EvidenciaModel> versoes = evidencias.historico(evdCod);
        if (versoes.isEmpty()) {
            throw new NaoEncontradoException("Evidência", evdCod);
        }
        List<EvidenciaDTO> historico = new ArrayList<>();
        for (EvidenciaModel e : versoes) {
            historico.add(toDTO(e));
        }
        return historico;
    }

    @Transactional(rollbackFor = Exception.class)
    public EvidenciaDTO registrar(EvidenciaDTO dto) {
        exigirAtividade(dto.atpCod());
        EvidenciaModel evidencia = mapper.toModel(dto);
        evidencia.setEvdCod(evidencias.proximoEvdCod());
        evidencia.setEvdCodSeq(1);
        evidencia.setStatus(EStatusEvidencia.PENDENTE_VALIDACAO);
        evidencia.setRegPesCod(pessoaAtual());
        evidencia.setData(LocalDateTime.now());
        evidencias.save(evidencia);
        marcarEmAndamentoPorEvidencia(dto.atpCod());
        return toDTO(evidencia);
    }

    @Transactional(rollbackFor = Exception.class)
    public EvidenciaDTO corrigir(Long evdCod, EvidenciaDTO dto) {
        EvidenciaModel corrente = evidencias.versaoCorrente(evdCod)
                .orElseThrow(() -> new NaoEncontradoException("Evidência", evdCod));
        if (corrente.getStatus() != EStatusEvidencia.CORRECAO_SOLICITADA) {
            throw new RegraNegocioException("Só evidências com correção solicitada podem ser corrigidas.");
        }
        exigirAtividade(dto.atpCod());

        int proximaSeq = evidencias.ultimaSeq(evdCod) + 1;
        EvidenciaModel nova = mapper.toModel(dto);
        nova.setEvdCod(evdCod);
        nova.setEvdCodSeq(proximaSeq);
        nova.setStatus(EStatusEvidencia.PENDENTE_VALIDACAO);
        nova.setRegPesCod(pessoaAtual());
        nova.setData(LocalDateTime.now());
        evidencias.save(nova);
        return toDTO(nova);
    }

    @Transactional(rollbackFor = Exception.class)
    public EvidenciaDTO avaliar(Long evdCod, EStatusEvidencia status, String motivo) {
        if (status != EStatusEvidencia.VALIDADA && status != EStatusEvidencia.CORRECAO_SOLICITADA) {
            throw new RegraNegocioException("A avaliação deve ser validar ou solicitar correção.");
        }
        EvidenciaModel corrente = evidencias.versaoCorrente(evdCod)
                .orElseThrow(() -> new NaoEncontradoException("Evidência", evdCod));
        if (corrente.getStatus() != EStatusEvidencia.PENDENTE_VALIDACAO) {
            throw new RegraNegocioException("Esta evidência já foi avaliada.");
        }
        if (status == EStatusEvidencia.CORRECAO_SOLICITADA && (motivo == null || motivo.isBlank())) {
            throw new RegraNegocioException("Informe o motivo da correção solicitada.");
        }
        corrente.setStatus(status);
        corrente.setMotivoCorrecao(
                status == EStatusEvidencia.CORRECAO_SOLICITADA ? motivo.trim() : null);
        evidencias.save(corrente);
        return toDTO(corrente);
    }

    @Transactional(readOnly = true)
    public List<AtividadeOpcaoDTO> atividadesDisponiveis() {
        PlanejamentoModel plano = vigenteDoCicloEmFoco();
        if (plano == null) {
            return List.of();
        }
        // Atividades por prática (cada uma já na ordem de ORDEM dentro da prática).
        Map<Long, List<AtividadePlanejadaModel>> porPratica = new HashMap<>();
        for (AtividadePlanejadaModel a : atividades.findByPlnCodOrderByOrdemAscAtpCodAsc(plano.getPlnCod())) {
            porPratica.computeIfAbsent(a.getPrtcCod(), k -> new ArrayList<>()).add(a);
        }

        // Emite na ordem estrutural: processos por ORDEM → práticas por ORDEM → atividades.
        List<AtividadeOpcaoDTO> opcoes = new ArrayList<>();
        for (ProcessoCicloModel proc : processosCiclo.findByCicCodOrderByOrdemAscPrccCodAsc(plano.getCicCod())) {
            for (PraticaCicloModel pratica : praticasCiclo.findByPrccCodOrderByOrdemAscPrtcCodAsc(proc.getPrccCod())) {
                for (AtividadePlanejadaModel a : porPratica.getOrDefault(pratica.getPrtcCod(), List.of())) {
                    opcoes.add(new AtividadeOpcaoDTO(
                            a.getAtpCod(), a.getNome(),
                            a.getPrtcCod(), pratica.getNome(),
                            proc.getPrccCod(), proc.getNome()));
                }
            }
        }
        return opcoes;
    }


    private PlanejamentoModel vigenteDoCicloEmFoco() {
        CiclosModel ciclo = cicloContexto.emFoco();
        if (ciclo == null) {
            return null;
        }
        return planejamentos
                .findByCicCodAndStatus(ciclo.getCicCod(), EStatusPlanejamento.PUBLICADO)
                .orElse(null);
    }

    private void exigirAtividade(Long atpCod) {
        if (atpCod == null || !atividades.existsById(atpCod)) {
            throw new NaoEncontradoException("Atividade planejada", atpCod);
        }
    }

    /** Registrar uma evidência reabre a atividade: PLANEJADA ou CONCLUIDA → EM_ANDAMENTO. */
    private void marcarEmAndamentoPorEvidencia(Long atpCod) {
        AtividadePlanejadaModel atv = atividades.findById(atpCod).orElse(null);
        if (atv == null) {
            return;
        }
        EStatusAtividade status = atv.getStatus();
        if (status == EStatusAtividade.PLANEJADA || status == EStatusAtividade.CONCLUIDA) {
            atv.setStatus(EStatusAtividade.EM_ANDAMENTO);
            atividades.save(atv);
        }
    }

    private EvidenciaDTO toDTO(EvidenciaModel e) {
        String atividadeNome = null;
        String praticaNome = null;
        String processoNome = null;
        if (e.getAtpCod() != null) {
            AtividadePlanejadaModel atividade = atividades.findById(e.getAtpCod()).orElse(null);
            if (atividade != null) {
                atividadeNome = atividade.getNome();
                PraticaCicloModel pratica = praticasCiclo.findById(atividade.getPrtcCod()).orElse(null);
                if (pratica != null) {
                    praticaNome = pratica.getNome();
                    ProcessoCicloModel processo = processosCiclo.findById(pratica.getPrccCod()).orElse(null);
                    if (processo != null) {
                        processoNome = processo.getNome();
                    }
                }
            }
        }
        return mapper.toDTO(e, atividadeNome, processoNome, praticaNome,
                rotuloArquivo(e.getArqCod()), rotuloResponsavel(e.getRegPesCod()));
    }

    private String rotuloArquivo(Long arqCod) {
        return arqCod == null ? null
                : arquivos.findById(arqCod).map(ArquivoModel::getNomeOriginal).orElse(null);
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
}

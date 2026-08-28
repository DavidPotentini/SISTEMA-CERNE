package com.github.davidpotentini.service.estruturaciclo;

import com.github.davidpotentini.comum.erro.NaoEncontradoException;
import com.github.davidpotentini.enums.EAtivoInativo;
import com.github.davidpotentini.model.estruturaciclo.PraticaCicloModel;
import com.github.davidpotentini.model.estruturaciclo.ProcessoCicloModel;
import com.github.davidpotentini.model.metodologia.PraticaModel;
import com.github.davidpotentini.model.metodologia.ProcessoModel;
import com.github.davidpotentini.repository.estruturaciclo.PraticaCicloRepository;
import com.github.davidpotentini.repository.estruturaciclo.ProcessoCicloRepository;
import com.github.davidpotentini.repository.metodologia.PraticaRepository;
import com.github.davidpotentini.repository.metodologia.ProcessoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Instância da estrutura (processos/práticas) por ciclo — o ciclo é dono da sua própria árvore, com
 * chave própria ({@code PRCC_COD}/{@code PRTC_COD}), imune a mudanças posteriores no template.
 *
 * <p>A estrutura é materializada <b>sob demanda</b>, a partir da metodologia, na primeira geração que
 * a referencia ({@link #garantirPratica}, chamado ao gerar indicadores e ao gerar o planejamento) — o
 * ciclo abre vazio, nada é copiado na abertura. É append-only: nunca recriada destrutivamente (senão
 * os códigos mudariam e os filhos — atividades/indicadores — ficariam órfãos); {@code garantirPratica}
 * casa por proveniência ({@code *_ORIGEM}), então cada prática do template entra uma vez só. O
 * {@code *_ORIGEM} é a correspondência template → instância, lida só aqui, nunca em exibição.
 */
@Service
public class EstruturaCicloService {

    private final ProcessoRepository processos;
    private final PraticaRepository praticas;
    private final ProcessoCicloRepository processosCiclo;
    private final PraticaCicloRepository praticasCiclo;

    public EstruturaCicloService(ProcessoRepository processos, PraticaRepository praticas,
                                 ProcessoCicloRepository processosCiclo,
                                 PraticaCicloRepository praticasCiclo) {
        this.processos = processos;
        this.praticas = praticas;
        this.processosCiclo = processosCiclo;
        this.praticasCiclo = praticasCiclo;
    }

    /**
     * Materializa a árvore ATIVA da metodologia no ciclo (o "Gerar do ciclo"): garante processos e
     * práticas ATIVOS na instância. Idempotente — casa por proveniência ({@code *_ORIGEM}), então
     * reexecutar não duplica; append-only. É o único ponto que cria estrutura do ciclo.
     */
    @Transactional(rollbackFor = Exception.class)
    public void materializarEstrutura(Long cicCod) {
        List<Long> prcCodsAtivos = new ArrayList<>();
        for (ProcessoModel proc : processos.findAllByOrderByOrdemAscPrcCodAsc()) {
            if (proc.getSituacao() == EAtivoInativo.ATIVO) {
                garantirProcesso(cicCod, proc.getPrcCod());
                prcCodsAtivos.add(proc.getPrcCod());
            }
        }
        if (prcCodsAtivos.isEmpty()) {
            return;
        }
        for (PraticaModel pratica : praticas.findByPrcCodIn(prcCodsAtivos)) {
            if (pratica.getSituacao() == EAtivoInativo.ATIVO) {
                garantirPratica(cicCod, pratica.getPrtCod());
            }
        }
    }

    /**
     * Devolve o {@code PRTC_COD} da instância que corresponde à prática {@code prtCodTemplate} no ciclo,
     * criando-a (e o processo, se preciso) sob demanda. Idempotente: casa pela proveniência.
     */
    @Transactional(rollbackFor = Exception.class)
    public Long garantirPratica(Long cicCod, Long prtCodTemplate) {
        PraticaCicloModel existente = praticasCiclo
                .findByCicCodAndPrtCodOrigem(cicCod, prtCodTemplate).orElse(null);
        if (existente != null) {
            return existente.getPrtcCod();
        }
        PraticaModel pratica = praticas.findById(prtCodTemplate)
                .orElseThrow(() -> new NaoEncontradoException("Prática", prtCodTemplate));
        Long prccCod = garantirProcesso(cicCod, pratica.getPrcCod());
        return copiarPratica(cicCod, prccCod, pratica).getPrtcCod();
    }

    private Long garantirProcesso(Long cicCod, Long prcCodTemplate) {
        ProcessoCicloModel existente = processosCiclo
                .findByCicCodAndPrcCodOrigem(cicCod, prcCodTemplate).orElse(null);
        if (existente != null) {
            return existente.getPrccCod();
        }
        ProcessoModel processo = processos.findById(prcCodTemplate)
                .orElseThrow(() -> new NaoEncontradoException("Processo", prcCodTemplate));
        return copiarProcesso(cicCod, processo).getPrccCod();
    }

    private ProcessoCicloModel copiarProcesso(Long cicCod, ProcessoModel processo) {
        ProcessoCicloModel copia = new ProcessoCicloModel();
        copia.setCicCod(cicCod);
        copia.setPrcCodOrigem(processo.getPrcCod());
        copia.setOrdem(processo.getOrdem());
        copia.setNome(processo.getNome());
        copia.setDescricao(processo.getDescricao());
        return processosCiclo.save(copia);
    }

    private PraticaCicloModel copiarPratica(Long cicCod, Long prccCod, PraticaModel pratica) {
        PraticaCicloModel copia = new PraticaCicloModel();
        copia.setCicCod(cicCod);
        copia.setPrccCod(prccCod);
        copia.setPrtCodOrigem(pratica.getPrtCod());
        copia.setNome(pratica.getNome());
        copia.setDescricao(pratica.getDescricao());
        return praticasCiclo.save(copia);
    }
}

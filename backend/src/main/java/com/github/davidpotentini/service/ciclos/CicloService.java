package com.github.davidpotentini.service.ciclos;

import com.github.davidpotentini.comum.ciclo.CicloContexto;
import com.github.davidpotentini.comum.erro.NaoEncontradoException;
import com.github.davidpotentini.comum.erro.RegraNegocioException;
import com.github.davidpotentini.dto.ciclos.CicloDTO;
import com.github.davidpotentini.enums.EStatusCiclo;
import com.github.davidpotentini.mapper.ciclos.CicloMapper;
import com.github.davidpotentini.model.ciclos.CiclosModel;
import com.github.davidpotentini.repository.ciclos.CiclosRepository;
import com.github.davidpotentini.service.painel.PainelOperacionalService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Ciclos da incubadora do usuário logado. Roda no schema do próprio tenant (o JWT já deixou o
 * {@code TenantContext} ativo), então {@code CICLOS} é lido e gravado direto — sem {@code callWithin}.
 * "Um por vez": ao criar um ciclo, o ativo anterior é encerrado; o foco ({@code EM_FOCO}) é único e
 * alternado pelo botão "Pôr em foco". Cada transição de unicidade dá {@code flush} antes de marcar o
 * novo, para não violar os índices parciais {@code UQ_CICLO_ATIVO}/{@code UQ_CICLO_EM_FOCO}.
 */
@Service
public class CicloService {

    private final CiclosRepository ciclos;
    private final CicloContexto cicloContexto;
    private final PainelOperacionalService painelOperacional;
    private final CicloMapper mapper;

    public CicloService(CiclosRepository ciclos, CicloContexto cicloContexto,
                        PainelOperacionalService painelOperacional, CicloMapper mapper) {
        this.ciclos = ciclos;
        this.cicloContexto = cicloContexto;
        this.painelOperacional = painelOperacional;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<CicloDTO> listar() {
        return mapper.toDTOList(ciclos.findAllByOrderByCicCodDesc());
    }

    /**
     * Novo ciclo nasce {@code ATIVO} e sem foco; encerra o ativo anterior antes de abrir. Abre
     * <b>vazio</b>: a estrutura (processos/práticas do ciclo) não é copiada aqui — ela é materializada
     * sob demanda, a partir da metodologia, na primeira geração (indicadores ou planejamento) via
     * {@code EstruturaCicloService.garantirPratica}.
     */
    @Transactional(rollbackFor = Exception.class)
    public CicloDTO criar(CicloDTO dto) {
        for (CiclosModel ativo : ciclos.findByStatus(EStatusCiclo.ATIVO)) {
            ativo.setStatus(EStatusCiclo.ENCERRADO);
            ciclos.saveAndFlush(ativo);
        }
        CiclosModel ciclo = mapper.toModel(dto);
        ciclo.setStatus(EStatusCiclo.ATIVO);
        ciclos.save(ciclo);
        return mapper.toDTO(ciclo);
    }

    /** Põe o ciclo em foco (o refletido nas telas); tira o foco do anterior. */
    @Transactional(rollbackFor = Exception.class)
    public CicloDTO porEmFoco(Long cicCod) {
        CiclosModel ciclo = buscar(cicCod);
        ciclos.findByEmFocoTrue().ifPresent(atual -> {
            if (!atual.getCicCod().equals(cicCod)) {
                atual.setEmFoco(false);
                ciclos.saveAndFlush(atual);
            }
        });
        ciclo.setEmFoco(true);
        ciclos.save(ciclo);
        return mapper.toDTO(ciclo);
    }

    /**
     * Encerra o ciclo (ação irreversível): passa {@code ATIVO → ENCERRADO}. Só o ciclo ativo encerra e
     * ele precisa estar em foco — assim as pendências lidas pelo painel (que olha o ciclo em foco) são
     * as do próprio alvo. Bloqueia enquanto houver qualquer pendência em aberto. Depois de encerrado,
     * a incubadora fica "entre ciclos" (somente leitura via {@code @EscopoCiclo}) até abrir o próximo.
     */
    @Transactional(rollbackFor = Exception.class)
    public CicloDTO encerrar(Long cicCod) {
        CiclosModel ciclo = buscar(cicCod);
        if (ciclo.getStatus() != EStatusCiclo.ATIVO) {
            throw new RegraNegocioException("Apenas o ciclo ativo pode ser encerrado.");
        }
        CiclosModel foco = cicloContexto.emFoco();
        if (foco == null || !foco.getCicCod().equals(cicCod)) {
            throw new RegraNegocioException("Ponha o ciclo ativo em foco para encerrá-lo.");
        }
        long impedimentos = painelOperacional.impedimentosDeEncerramento();
        if (impedimentos > 0) {
            throw new RegraNegocioException("Há " + impedimentos
                    + " pendência(s) em aberto neste ciclo. Resolva-as no Painel Operacional antes de encerrar.");
        }
        ciclo.setStatus(EStatusCiclo.ENCERRADO);
        ciclos.save(ciclo);
        return mapper.toDTO(ciclo);
    }

    private CiclosModel buscar(Long cicCod) {
        return ciclos.findById(cicCod)
                .orElseThrow(() -> new NaoEncontradoException("Ciclo", cicCod));
    }
}

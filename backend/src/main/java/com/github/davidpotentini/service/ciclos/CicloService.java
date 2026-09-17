package com.github.davidpotentini.service.ciclos;

import com.github.davidpotentini.comum.ciclo.CicloContexto;
import com.github.davidpotentini.comum.erro.NaoEncontradoException;
import com.github.davidpotentini.comum.erro.RegraNegocioException;
import com.github.davidpotentini.dto.ciclos.CicloDTO;
import com.github.davidpotentini.enums.EStatusCiclo;
import com.github.davidpotentini.mapper.ciclos.CicloMapper;
import com.github.davidpotentini.model.ciclos.CiclosModel;
import com.github.davidpotentini.repository.ciclos.CiclosRepository;
import com.github.davidpotentini.service.painel.PendenciasService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Ciclos da incubadora. Cada transição de unicidade (ativo, em foco) dá {@code flush} antes de marcar
 * o novo, para não violar os índices parciais {@code UQ_CICLO_ATIVO}/{@code UQ_CICLO_EM_FOCO}.
 */
@Service
public class CicloService {

    private final CiclosRepository ciclos;
    private final CicloContexto cicloContexto;
    private final PendenciasService pendencias;
    private final CicloMapper mapper;

    public CicloService(CiclosRepository ciclos, CicloContexto cicloContexto,
                        PendenciasService pendencias, CicloMapper mapper) {
        this.ciclos = ciclos;
        this.cicloContexto = cicloContexto;
        this.pendencias = pendencias;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<CicloDTO> listar() {
        return mapper.toDTOList(ciclos.findAllByOrderByCicCodDesc());
    }

    /**
     * Encerra o ativo anterior e abre <b>vazio</b>: a estrutura não é copiada aqui — é materializada
     * sob demanda, a partir da metodologia, na primeira geração (indicadores ou planejamento).
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
     * Encerra o ciclo (irreversível). Exige que o alvo esteja em foco — assim as pendências lidas
     * (que olham o ciclo em foco) são as do próprio alvo — e bloqueia se houver pendência em aberto.
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
        long impedimentos = pendencias.impedimentosDeEncerramento();
        if (impedimentos > 0) {
            throw new RegraNegocioException("Há " + impedimentos
                    + " pendência(s) em aberto neste ciclo. Resolva-as em Pendências antes de encerrar.");
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

package com.github.davidpotentini.service.ciclos;

import com.github.davidpotentini.comum.erro.NaoEncontradoException;
import com.github.davidpotentini.dto.ciclos.CicloDTO;
import com.github.davidpotentini.enums.EStatusCiclo;
import com.github.davidpotentini.mapper.ciclos.CicloMapper;
import com.github.davidpotentini.model.ciclos.CiclosModel;
import com.github.davidpotentini.repository.ciclos.CiclosRepository;
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
    private final CicloMapper mapper;

    public CicloService(CiclosRepository ciclos, CicloMapper mapper) {
        this.ciclos = ciclos;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<CicloDTO> listar() {
        return mapper.toDTOList(ciclos.findAllByOrderByCicCodDesc());
    }

    /** Novo ciclo nasce {@code ATIVO} e sem foco; encerra o ativo anterior antes de abrir. */
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

    private CiclosModel buscar(Long cicCod) {
        return ciclos.findById(cicCod)
                .orElseThrow(() -> new NaoEncontradoException("Ciclo", cicCod));
    }
}

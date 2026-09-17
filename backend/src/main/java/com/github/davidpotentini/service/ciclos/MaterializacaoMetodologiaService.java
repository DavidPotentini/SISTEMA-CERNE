package com.github.davidpotentini.service.ciclos;

import com.github.davidpotentini.comum.ciclo.CicloContexto;
import com.github.davidpotentini.comum.erro.RegraNegocioException;
import com.github.davidpotentini.dto.ciclos.CicloDTO;
import com.github.davidpotentini.dto.ciclos.EmpreendimentoOpcaoDTO;
import com.github.davidpotentini.dto.ciclos.GerarCicloOpcoesDTO;
import com.github.davidpotentini.mapper.ciclos.CicloMapper;
import com.github.davidpotentini.model.ciclos.CicloEmpreendimentoModel;
import com.github.davidpotentini.model.ciclos.CiclosModel;
import com.github.davidpotentini.model.empreendimentos.EmpreendimentosModel;
import com.github.davidpotentini.repository.ciclos.CicloEmpreendimentoRepository;
import com.github.davidpotentini.repository.empreendimentos.EmpreendimentosRepository;
import com.github.davidpotentini.service.estruturaciclo.EstruturaCicloService;
import com.github.davidpotentini.service.indicador.IndicadorService;
import com.github.davidpotentini.service.planejamento.PlanejamentoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * "Gerar do ciclo": orquestra as três etapas na ordem — estrutura, indicadores e atividades
 * planejadas — a partir da metodologia, para o ciclo em foco.
 */
@Service
public class MaterializacaoMetodologiaService {

    private final CicloContexto cicloContexto;
    private final EstruturaCicloService estruturaCiclo;
    private final IndicadorService indicadorService;
    private final PlanejamentoService planejamentoService;
    private final EmpreendimentosRepository empreendimentos;
    private final CicloEmpreendimentoRepository cicloEmpreendimentos;
    private final CicloMapper mapper;

    public MaterializacaoMetodologiaService(CicloContexto cicloContexto,
                                            EstruturaCicloService estruturaCiclo,
                                            IndicadorService indicadorService,
                                            PlanejamentoService planejamentoService,
                                            EmpreendimentosRepository empreendimentos,
                                            CicloEmpreendimentoRepository cicloEmpreendimentos,
                                            CicloMapper mapper) {
        this.cicloContexto = cicloContexto;
        this.estruturaCiclo = estruturaCiclo;
        this.indicadorService = indicadorService;
        this.planejamentoService = planejamentoService;
        this.empreendimentos = empreendimentos;
        this.cicloEmpreendimentos = cicloEmpreendimentos;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public CicloDTO alvo() {
        CiclosModel ciclo = cicloContexto.emFoco();
        return ciclo == null ? null : mapper.toDTO(ciclo);
    }

    @Transactional(readOnly = true)
    public GerarCicloOpcoesDTO opcoesGerar() {
        CiclosModel ciclo = cicloContexto.emFoco();
        List<EmpreendimentoOpcaoDTO> incubadas = new ArrayList<>();
        List<Long> selecionadas = new ArrayList<>();
        if (ciclo != null) {
            Set<Long> membros = new LinkedHashSet<>();
            for (CicloEmpreendimentoModel ce : cicloEmpreendimentos.findByCicCod(ciclo.getCicCod())) {
                membros.add(ce.getEmpCod());
            }
            for (EmpreendimentosModel e : empreendimentos.findAllByOrderByNomeAsc()) {
                if (membros.contains(e.getEmpCod())) {
                    incubadas.add(new EmpreendimentoOpcaoDTO(e.getEmpCod(), e.getNome()));
                    selecionadas.add(e.getEmpCod());
                }
            }
        }
        return new GerarCicloOpcoesDTO(incubadas, selecionadas);
    }

    @Transactional(rollbackFor = Exception.class)
    public CicloDTO materializarMetodologia(List<Long> empCods) {
        CiclosModel ciclo = cicloContexto.emFoco();
        if (ciclo == null) {
            throw new RegraNegocioException("Não há ciclo em foco. Abra um ciclo antes de gerar.");
        }
        // Trava: regerar substituiria o plano e os indicadores gerados; não sobrepor edições já feitas.
        if (planejamentoService.cicloTemEdicoes(ciclo.getCicCod())
                || indicadorService.cicloTemEdicoes(ciclo.getCicCod())) {
            throw new RegraNegocioException(
                    "O ciclo já tem edições (atividades ajustadas/complementares, execução, evidências, "
                    + "indicadores complementares, responsáveis ou metas). Gerar de novo substituiria essas "
                    + "edições. Abra um novo ciclo para regerar do zero.");
        }
        estruturaCiclo.materializarEstrutura(ciclo.getCicCod());
        indicadorService.gerarDoCiclo();
        planejamentoService.gerarDoCiclo(empCods);
        return mapper.toDTO(ciclo);
    }
}

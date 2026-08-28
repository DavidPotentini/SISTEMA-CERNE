package com.github.davidpotentini.service.ciclos;

import com.github.davidpotentini.comum.ciclo.CicloContexto;
import com.github.davidpotentini.comum.erro.RegraNegocioException;
import com.github.davidpotentini.dto.ciclos.CicloDTO;
import com.github.davidpotentini.mapper.ciclos.CicloMapper;
import com.github.davidpotentini.model.ciclos.CiclosModel;
import com.github.davidpotentini.service.estruturaciclo.EstruturaCicloService;
import com.github.davidpotentini.service.indicador.IndicadorService;
import com.github.davidpotentini.service.planejamento.PlanejamentoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * "Materializar a metodologia no ciclo" (o "Gerar do ciclo"): traz a metodologia para o ciclo em foco,
 * num ato só. Orquestra as três etapas na ordem — estrutura (processos/práticas), indicadores e
 * atividades planejadas —, todas a partir da metodologia. É o único ponto que cria a estrutura do
 * ciclo; indicadores e planejamento apenas penduram nela. Disparado pelo botão na tela de Metodologia;
 * a guarda de ciclo encerrado (só leitura) vem do {@code @EscopoCiclo} no controller.
 */
@Service
public class MaterializacaoMetodologiaService {

    private final CicloContexto cicloContexto;
    private final EstruturaCicloService estruturaCiclo;
    private final IndicadorService indicadorService;
    private final PlanejamentoService planejamentoService;
    private final CicloMapper mapper;

    public MaterializacaoMetodologiaService(CicloContexto cicloContexto,
                                            EstruturaCicloService estruturaCiclo,
                                            IndicadorService indicadorService,
                                            PlanejamentoService planejamentoService, CicloMapper mapper) {
        this.cicloContexto = cicloContexto;
        this.estruturaCiclo = estruturaCiclo;
        this.indicadorService = indicadorService;
        this.planejamentoService = planejamentoService;
        this.mapper = mapper;
    }

    /** Ciclo que receberá a materialização (o em foco), ou {@code null} se não houver — alimenta o botão. */
    @Transactional(readOnly = true)
    public CicloDTO alvo() {
        CiclosModel ciclo = cicloContexto.emFoco();
        return ciclo == null ? null : mapper.toDTO(ciclo);
    }

    /** Materializa a metodologia no ciclo em foco: estrutura + indicadores + atividades planejadas. */
    @Transactional(rollbackFor = Exception.class)
    public CicloDTO materializarMetodologia() {
        CiclosModel ciclo = cicloContexto.emFoco();
        if (ciclo == null) {
            throw new RegraNegocioException("Não há ciclo em foco. Abra um ciclo antes de gerar.");
        }
        estruturaCiclo.materializarEstrutura(ciclo.getCicCod());
        indicadorService.gerarDoCiclo();
        planejamentoService.gerarDoCiclo();
        return mapper.toDTO(ciclo);
    }
}

package com.github.davidpotentini.mapper.planejamento;

import com.github.davidpotentini.dto.planejamento.AtividadePlanejadaDTO;
import com.github.davidpotentini.dto.planejamento.PlanPraticaDTO;
import com.github.davidpotentini.dto.planejamento.PlanProcessoDTO;
import com.github.davidpotentini.dto.planejamento.PlanejamentoDTO;
import com.github.davidpotentini.model.metodologia.PraticaModel;
import com.github.davidpotentini.model.metodologia.ProcessoModel;
import com.github.davidpotentini.model.planejamento.AtividadePlanejadaModel;
import com.github.davidpotentini.model.planejamento.PlanejamentoModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * Conversão de planejamentos. No cabeçalho, os rótulos ({@code cicloNome}, {@code modeloNome},
 * {@code responsavel}) e o resumo de execução ({@code totalAtividades}/{@code concluidas}/
 * {@code progresso}) são calculados no service e passados prontos. A estrutura (processo/prática) é
 * herdada da metodologia — o service carrega práticas/atividades e as passa ao montar os DTOs de árvore.
 */
@Mapper(componentModel = "spring")
public interface PlanejamentoMapper {

    PlanejamentoDTO toDTO(PlanejamentoModel plano, String cicloNome, String modeloNome,
                          String responsavel, int totalAtividades, int concluidas, int progresso);

    // ---- atividade planejada ----

    AtividadePlanejadaDTO toDTO(AtividadePlanejadaModel atividade);

    List<AtividadePlanejadaDTO> toDTOList(List<AtividadePlanejadaModel> atividades);

    /** Inclusão (complementar): plnCod/prtCod vêm da rota; origem/status nascem no service. */
    @Mapping(target = "atpCod", ignore = true)
    @Mapping(target = "plnCod", ignore = true)
    @Mapping(target = "origem", ignore = true)
    @Mapping(target = "prtCod", ignore = true)
    @Mapping(target = "status", ignore = true)
    AtividadePlanejadaModel toModel(AtividadePlanejadaDTO dto);

    /** Ajuste: nome/descrição/responsável/prazo; preserva código, vínculo, origem, status e empCod. */
    @Mapping(target = "atpCod", ignore = true)
    @Mapping(target = "plnCod", ignore = true)
    @Mapping(target = "origem", ignore = true)
    @Mapping(target = "prtCod", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "empCod", ignore = true)
    void atualizar(AtividadePlanejadaDTO dto, @MappingTarget AtividadePlanejadaModel atividade);

    // ---- árvore (herdada da metodologia + atividades) ----

    PlanPraticaDTO toDTO(PraticaModel pratica, List<AtividadePlanejadaDTO> atividades);

    PlanProcessoDTO toDTO(ProcessoModel processo, List<PlanPraticaDTO> praticas);
}

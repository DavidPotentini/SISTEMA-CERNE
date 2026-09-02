package com.github.davidpotentini.mapper.planejamento;

import com.github.davidpotentini.dto.planejamento.AtividadePlanejadaDTO;
import com.github.davidpotentini.dto.planejamento.PlanGrupoDTO;
import com.github.davidpotentini.dto.planejamento.PlanPraticaDTO;
import com.github.davidpotentini.dto.planejamento.PlanProcessoDTO;
import com.github.davidpotentini.dto.planejamento.PlanejamentoDTO;
import com.github.davidpotentini.model.estruturaciclo.PraticaCicloModel;
import com.github.davidpotentini.model.estruturaciclo.ProcessoCicloModel;
import com.github.davidpotentini.model.planejamento.AtividadePlanejadaModel;
import com.github.davidpotentini.model.planejamento.PlanejamentoModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * Conversão de planejamentos. No cabeçalho, os rótulos ({@code cicloNome}, {@code responsavel}) e o
 * resumo de execução ({@code totalAtividades}/{@code concluidas}/
 * {@code progresso}) são calculados no service e passados prontos. A estrutura (processo/prática) vem
 * da instância do ciclo ({@code PROCESSOS_CICLO}/{@code PRATICAS_CICLO}) — o service carrega os nós e
 * as atividades e os passa ao montar os DTOs de árvore ({@code prtcCod}/{@code prccCod}).
 */
@Mapper(componentModel = "spring")
public interface PlanejamentoMapper {

    PlanejamentoDTO toDTO(PlanejamentoModel plano, String cicloNome,
                          String responsavel, int totalAtividades, int concluidas, int progresso);

    // ---- atividade planejada ----

    AtividadePlanejadaDTO toDTO(AtividadePlanejadaModel atividade, String responsavelNome);

    /** Inclusão (complementar): plnCod/prtcCod/agrcCod/ordem vêm da rota/service; origem/status nascem no service. */
    @Mapping(target = "atpCod", ignore = true)
    @Mapping(target = "plnCod", ignore = true)
    @Mapping(target = "origem", ignore = true)
    @Mapping(target = "prtcCod", ignore = true)
    @Mapping(target = "agrcCod", ignore = true)
    @Mapping(target = "ordem", ignore = true)
    @Mapping(target = "status", ignore = true)
    AtividadePlanejadaModel toModel(AtividadePlanejadaDTO dto);

    /** Ajuste: nome/descrição/responsável/prazo; preserva código, vínculo, grupo, ordem, origem, status e empCod. */
    @Mapping(target = "atpCod", ignore = true)
    @Mapping(target = "plnCod", ignore = true)
    @Mapping(target = "origem", ignore = true)
    @Mapping(target = "prtcCod", ignore = true)
    @Mapping(target = "agrcCod", ignore = true)
    @Mapping(target = "ordem", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "empCod", ignore = true)
    void atualizar(AtividadePlanejadaDTO dto, @MappingTarget AtividadePlanejadaModel atividade);

    // ---- árvore (instância do ciclo + grupos + atividades) ----

    PlanPraticaDTO toDTO(PraticaCicloModel pratica, List<PlanGrupoDTO> grupos);

    PlanProcessoDTO toDTO(ProcessoCicloModel processo, List<PlanPraticaDTO> praticas);
}

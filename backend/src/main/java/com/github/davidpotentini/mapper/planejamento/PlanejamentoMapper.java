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

@Mapper(componentModel = "spring")
public interface PlanejamentoMapper {

    PlanejamentoDTO toDTO(PlanejamentoModel plano, String cicloNome,
                          String responsavel, int totalAtividades, int concluidas, int progresso);

    AtividadePlanejadaDTO toDTO(AtividadePlanejadaModel atividade, String empreendimentoNome, String responsavelNome);

    @Mapping(target = "atpCod", ignore = true)
    @Mapping(target = "plnCod", ignore = true)
    @Mapping(target = "origem", ignore = true)
    @Mapping(target = "prtcCod", ignore = true)
    @Mapping(target = "agrcCod", ignore = true)
    @Mapping(target = "ordem", ignore = true)
    @Mapping(target = "status", ignore = true)
    AtividadePlanejadaModel toModel(AtividadePlanejadaDTO dto);

    @Mapping(target = "atpCod", ignore = true)
    @Mapping(target = "plnCod", ignore = true)
    @Mapping(target = "origem", ignore = true)
    @Mapping(target = "prtcCod", ignore = true)
    @Mapping(target = "agrcCod", ignore = true)
    @Mapping(target = "ordem", ignore = true)
    @Mapping(target = "status", ignore = true)
    void atualizar(AtividadePlanejadaDTO dto, @MappingTarget AtividadePlanejadaModel atividade);

    PlanPraticaDTO toDTO(PraticaCicloModel pratica, List<PlanGrupoDTO> grupos);

    PlanProcessoDTO toDTO(ProcessoCicloModel processo, List<PlanPraticaDTO> praticas);
}

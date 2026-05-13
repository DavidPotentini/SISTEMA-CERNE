package com.github.davidpotentini.cerne2.mapper.validacaohipotese;

import com.github.davidpotentini.cerne2.dto.validacaohipotese.HipoteseDTO;
import com.github.davidpotentini.cerne2.mapper.EntityReferenceResolver;
import com.github.davidpotentini.cerne2.models.validacaohipotese.HipoteseModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = EntityReferenceResolver.class)
public interface HipoteseMapper {

    @Mapping(source = "quadroValidacaoHipoteseModel.qvhCod", target = "qvhCod")
    HipoteseDTO toDTO(HipoteseModel hipoteseModel);

    List<HipoteseDTO> toDTOList(List<HipoteseModel> hipoteseModelList);

    @Mapping(source = "hipoteseDTO.qvhCod", target = "quadroValidacaoHipoteseModel")
    HipoteseModel toModel(HipoteseDTO hipoteseDTO, Long hipCod);

    // novo — usado pelo QuadroValidacaoHipoteseMapper na lista
    @Mapping(source = "hipoteseDTO.qvhCod", target = "quadroValidacaoHipoteseModel")
    HipoteseModel toModel(HipoteseDTO hipoteseDTO);

    List<HipoteseModel> toModelList(List<HipoteseDTO> hipoteseDTOList);
}

package com.github.davidpotentini.cerne2.mapper.validacaohipotese;

import com.github.davidpotentini.cerne2.dto.validacaohipotese.QuadroValidacaoHipoteseDTO;
import com.github.davidpotentini.cerne2.mapper.EntityReferenceResolver;
import com.github.davidpotentini.cerne2.models.validacaohipotese.QuadroValidacaoHipoteseModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {EntityReferenceResolver.class, HipoteseMapper.class})
public interface QuadroValidacaoHipoteseMapper {

    @Mapping(source = "incubadasModel.incCod", target = "incCod")
    @Mapping(source = "hipoteseModelList", target = "hipoteseDTOList")
    QuadroValidacaoHipoteseDTO toDTO(QuadroValidacaoHipoteseModel quadroValidacaoHipoteseModel);

    List<QuadroValidacaoHipoteseDTO> toDTOList(List<QuadroValidacaoHipoteseModel> quadroValidacaoHipoteseModelList);

    @Mapping(source = "quadroValidacaoHipoteseDTO.incCod", target = "incubadasModel")
    @Mapping(source = "quadroValidacaoHipoteseDTO.hipoteseDTOList", target = "hipoteseModelList")
    QuadroValidacaoHipoteseModel toModel(QuadroValidacaoHipoteseDTO quadroValidacaoHipoteseDTO, Long qvhCod);


}

package com.github.davidpotentini.cerne2.mapper.informacoesgeraisincubadas;

import com.github.davidpotentini.cerne2.dto.informacoesgeraisincubadas.IncubadasDTO;
import com.github.davidpotentini.cerne2.mapper.endereco.EnderecoMapper;
import com.github.davidpotentini.cerne2.models.informacoesgeraisincubadas.IncubadasModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = EnderecoMapper.class)
public interface IncubadasMapper {

    @Mapping(source = "enderecosModel", target = "enderecoDTO")
    @Mapping(source = "EStatusIncubacao", target = "eStatusIncubacao")
    IncubadasDTO toDTO(IncubadasModel incubadasModel);

    List<IncubadasDTO> toDTOList(List<IncubadasModel> incubadasModelList);

    // enderecosModel é montado na service (precisa do endCod existente em atualizações)
    @Mapping(target = "incCod", source = "incCod")
    @Mapping(source = "incubadasDTO.eStatusIncubacao", target = "EStatusIncubacao")
    @Mapping(target = "enderecosModel", ignore = true)
    @Mapping(target = "pessoasModelList", ignore = true)
    @Mapping(target = "ambienteCanvasModelList", ignore = true)
    @Mapping(target = "quadroValidacaoHipoteseModelList", ignore = true)
    IncubadasModel toModel(IncubadasDTO incubadasDTO, Long incCod);
}

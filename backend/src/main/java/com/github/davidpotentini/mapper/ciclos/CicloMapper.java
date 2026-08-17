package com.github.davidpotentini.mapper.ciclos;

import com.github.davidpotentini.dto.ciclos.CicloDTO;
import com.github.davidpotentini.model.ciclos.CiclosModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Conversão do ciclo nos dois sentidos. Na escrita, {@code cicCod} é gerado e {@code status} é
 * definido pelo service ("nasce ATIVO"), então ambos são ignorados pelo mapper.
 */
@Mapper(componentModel = "spring")
public interface CicloMapper {

    CicloDTO toDTO(CiclosModel ciclo);

    List<CicloDTO> toDTOList(List<CiclosModel> ciclos);

    @Mapping(target = "cicCod", ignore = true)
    @Mapping(target = "status", ignore = true)
    CiclosModel toModel(CicloDTO dto);

    List<CiclosModel> toModelList(List<CicloDTO> dtos);
}

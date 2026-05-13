package com.github.davidpotentini.cerne2.service.informacoesgeraisincubadas;

import com.github.davidpotentini.cerne2.dto.endereco.response.EnderecoDTOResponse;
import com.github.davidpotentini.cerne2.dto.informacoesgeraisincubadas.request.IncubadasDTORequest;
import com.github.davidpotentini.cerne2.dto.informacoesgeraisincubadas.respose.IncubadasDTOResponse;
import com.github.davidpotentini.cerne2.models.informacoesgeraisincubadas.IncubadasModel;
import com.github.davidpotentini.cerne2.repository.enderecos.EnderecoRepository;
import com.github.davidpotentini.cerne2.repository.informacoesgeraisincubadas.IncubadasRepository;
import com.github.davidpotentini.cerne2.service.enderecos.EnderecoService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class IncubadasService {

    private final IncubadasRepository incubadasRepository;
    private final EnderecoService enderecoService;

    public IncubadasService(IncubadasRepository incubadasRepository, EnderecoService enderecoService) {
        this.incubadasRepository = incubadasRepository;
        this.enderecoService = enderecoService;
    }

    public List<IncubadasDTOResponse> findList() {
        List<IncubadasModel> incubadasModelList = incubadasRepository.findAll();

        List<IncubadasDTOResponse> incubadasDTOResponses = new ArrayList<>();

        for (IncubadasModel i: incubadasModelList){
            incubadasDTOResponses.add(mapToIncubadasDTO(i));
        }

        return incubadasDTOResponses;
    }

    public IncubadasDTOResponse findById(Long incCod) {
        return mapToIncubadasDTO(incubadasRepository.findById(incCod)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }


    //VERIFICAR SE QUANDO ATUALIZA O ENDEREÇO UM NOVO REGISTRO É CRIADO
    @Transactional(rollbackFor = Exception.class)
    public IncubadasDTOResponse save(IncubadasDTORequest incubadasDTORequest, Long incCod){
        return mapToIncubadasDTO(incubadasRepository.save(mapToIncubadasModel(incubadasDTORequest, incCod)));
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long incCod){
        IncubadasModel incubadasModel = incubadasRepository.findById(incCod)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        enderecoService.delete(incubadasModel.getEnderecosModel().getEndCod());

        incubadasRepository.delete(incubadasModel);
    }

    private IncubadasDTOResponse mapToIncubadasDTO (IncubadasModel incubadasModel) {
        return new IncubadasDTOResponse(
                incubadasModel.getIncCod(),
                incubadasModel.getNome(),
                incubadasModel.getDataInicioIncubacao(),
                incubadasModel.getEmail(),
                incubadasModel.getEStatusIncubacao(),
                incubadasModel.getDescricao(),
                incubadasModel.getDocumentacao(),
                enderecoService.mapToEnderecoDTO(incubadasModel.getEnderecosModel())
        );
    }

    private IncubadasModel mapToIncubadasModel (IncubadasDTORequest incubadasDTORequest, Long incCod) {
        IncubadasModel incubadasModel = new IncubadasModel();

        incubadasModel.setIncCod(incCod);
        incubadasModel.setNome(incubadasDTORequest.nome());
        incubadasModel.setDataInicioIncubacao(incubadasDTORequest.dataInicioIncubacao());
        incubadasModel.setEmail(incubadasDTORequest.email());
        incubadasModel.setEStatusIncubacao(incubadasDTORequest.eStatusIncubacao());
        incubadasModel.setDescricao(incubadasDTORequest.descricao());
        incubadasModel.setDocumentacao(incubadasDTORequest.documentacao());

        Long endCod = null;

        if (incCod != null) {
            endCod = incubadasRepository.findById(incCod).
                    orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)).getEnderecosModel().getEndCod();
        }

        incubadasModel.setEnderecosModel(enderecoService.mapToEnderecosModel(incubadasDTORequest.enderecoDTORequest(), endCod));

        return incubadasModel;
    }
}

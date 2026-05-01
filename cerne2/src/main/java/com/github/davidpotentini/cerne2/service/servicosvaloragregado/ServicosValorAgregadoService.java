package com.github.davidpotentini.cerne2.service.servicosvaloragregado;

import com.github.davidpotentini.cerne2.dto.servicosvaloragregado.request.ServicosValorAgregadoDTORequest;
import com.github.davidpotentini.cerne2.dto.servicosvaloragregado.response.ServicosValorAgregadoDTOResponse;
import com.github.davidpotentini.cerne2.models.servicosvaloragregado.ServicosValorAgregadoModel;
import com.github.davidpotentini.cerne2.repository.servicosvaloragregado.ServicosValorAgregadoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class ServicosValorAgregadoService {

    private final ServicosValorAgregadoRepository servicosValorAgregadoRepository;

    public ServicosValorAgregadoService(ServicosValorAgregadoRepository servicosValorAgregadoRepository){
        this.servicosValorAgregadoRepository = servicosValorAgregadoRepository;
    }


    public List<ServicosValorAgregadoDTOResponse> findList(){
        List<ServicosValorAgregadoModel> servicosValorAgregadoModel = servicosValorAgregadoRepository.findAll();

        List<ServicosValorAgregadoDTOResponse> servicosValorAgregadoDTOResponses = new ArrayList<>();

        for (ServicosValorAgregadoModel s: servicosValorAgregadoModel){
            servicosValorAgregadoDTOResponses.add(mapToServicosValorAgregadoModelDTO(s));
        }

        return servicosValorAgregadoDTOResponses;
    }

    public ServicosValorAgregadoDTOResponse findById(Long pesCod){
        return mapToServicosValorAgregadoModelDTO(servicosValorAgregadoRepository.findById(pesCod).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    public ServicosValorAgregadoDTOResponse save(ServicosValorAgregadoDTORequest servicosValorAgregadoDTORequest, Long servCod){
        return mapToServicosValorAgregadoModelDTO(servicosValorAgregadoRepository.save(mapToServicoValorAgregadoModel(servicosValorAgregadoDTORequest, servCod)));
    }

    public void delete(Long pesCod){
        ServicosValorAgregadoModel servicosValorAgregadoModel = servicosValorAgregadoRepository.findById(pesCod).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        servicosValorAgregadoRepository.delete(servicosValorAgregadoModel);
    }

    private ServicosValorAgregadoDTOResponse mapToServicosValorAgregadoModelDTO(ServicosValorAgregadoModel servicosValorAgregadoModel){
        return new ServicosValorAgregadoDTOResponse(servicosValorAgregadoModel.getServCod(),
                                            servicosValorAgregadoModel.getServTitulo(),
                                            servicosValorAgregadoModel.getServDesc(),
                                            servicosValorAgregadoModel.getServCusto(),
                                            servicosValorAgregadoModel.getServCondContratacao(),
                                            servicosValorAgregadoModel.getServAnexos());
    }

    public ServicosValorAgregadoModel mapToServicoValorAgregadoModel(ServicosValorAgregadoDTORequest servicosValorAgregadoDTORequest, Long servCod){
        ServicosValorAgregadoModel servicosValorAgregadoModel = new ServicosValorAgregadoModel();

        servicosValorAgregadoModel.setServCod(servCod);
        servicosValorAgregadoModel.setServTitulo(servicosValorAgregadoDTORequest.servTitulo());
        servicosValorAgregadoModel.setServDesc(servicosValorAgregadoDTORequest.servDesc());
        servicosValorAgregadoModel.setServCusto(servicosValorAgregadoDTORequest.servCusto());
        servicosValorAgregadoModel.setServCondContratacao(servicosValorAgregadoDTORequest.servCondContratacao());
        servicosValorAgregadoModel.setServAnexos(servicosValorAgregadoDTORequest.servAnexos());

        return servicosValorAgregadoModel;
    }
}

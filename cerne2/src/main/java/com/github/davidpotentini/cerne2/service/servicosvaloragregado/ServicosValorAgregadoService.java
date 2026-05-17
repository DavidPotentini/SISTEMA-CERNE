package com.github.davidpotentini.cerne2.service.servicosvaloragregado;

import com.github.davidpotentini.cerne2.dto.servicosvaloragregado.ServicosValorAgregadoDTO;
import com.github.davidpotentini.cerne2.mapper.servicosvaloragregado.ServicosValorAgregadoMapper;
import com.github.davidpotentini.cerne2.models.servicosvaloragregado.ServicosValorAgregadoModel;
import com.github.davidpotentini.cerne2.repository.servicosvaloragregado.ServicosValorAgregadoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ServicosValorAgregadoService {

    private final ServicosValorAgregadoRepository servicosValorAgregadoRepository;
    private final ServicosValorAgregadoMapper servicosValorAgregadoMapper;

    public ServicosValorAgregadoService(ServicosValorAgregadoRepository servicosValorAgregadoRepository,
                                        ServicosValorAgregadoMapper servicosValorAgregadoMapper) {
        this.servicosValorAgregadoRepository = servicosValorAgregadoRepository;
        this.servicosValorAgregadoMapper = servicosValorAgregadoMapper;
    }

    public List<ServicosValorAgregadoDTO> findList(){
        return servicosValorAgregadoMapper.toDTOList(servicosValorAgregadoRepository.findAll());
    }

    public ServicosValorAgregadoDTO findById(Long servCod){
        return servicosValorAgregadoMapper.toDTO(servicosValorAgregadoRepository.findById(servCod)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @Transactional(rollbackFor = Exception.class)
    public ServicosValorAgregadoDTO save(ServicosValorAgregadoDTO servicosValorAgregadoDTO, Long servCod){
        return servicosValorAgregadoMapper.toDTO(servicosValorAgregadoRepository
                .save(servicosValorAgregadoMapper.toModel(servicosValorAgregadoDTO, servCod)));
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long servCod){

        servicosValorAgregadoRepository.deleteById(servCod);
    }
}

package com.github.davidpotentini.cerne2.service.canvas;

import com.github.davidpotentini.cerne2.dto.canvas.*;
import com.github.davidpotentini.cerne2.mapper.canvas.*;
import com.github.davidpotentini.cerne2.models.canvas.BusinessModelCanvasModel;
import com.github.davidpotentini.cerne2.models.canvas.LeanCanvasModel;
import com.github.davidpotentini.cerne2.models.canvas.ValuePropostionCanvasModel;
import com.github.davidpotentini.cerne2.models.canvas.ids.ChannelImplementationCanvasId;
import com.github.davidpotentini.cerne2.models.canvas.ids.CustomerPersonasCanvasId;
import com.github.davidpotentini.cerne2.repository.canvas.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CanvasService {

    private final AmbienteCanvasRepository ambienteCanvasRepository;
    private final BusinessModelCanvasRepository businessModelCanvasRepository;
    private final ChannelImplementationCanvasRepository channelImplementationCanvasRepository;
    private final CustomerPersonasCanvasRepository customerPersonasCanvasRepository;
    private final LeanCanvasRepository leanCanvasRepository;
    private final ValuePropositionCanvasRepository valuePropositionCanvasRepository;
    private final AmbienteCanvasMapper ambienteCanvasMapper;
    private final BusinessModelCanvasMapper businessModelCanvasMapper;
    private final ChannelImplementationCanvasMapper channelImplementationCanvasMapper;
    private final CustomerPersonasCanvasMapper customerPersonasCanvasMapper;
    private final LeanCanvasMapper leanCanvasMapper;
    private final ValuePropositionCanvasMapper valuePropositionCanvasMapper;

    public CanvasService(AmbienteCanvasRepository ambienteCanvasRepository,
                         BusinessModelCanvasRepository businessModelCanvasRepository,
                         ChannelImplementationCanvasRepository channelImplementationCanvasRepository,
                         CustomerPersonasCanvasRepository customerPersonasCanvasRepository,
                         LeanCanvasRepository leanCanvasRepository,
                         ValuePropositionCanvasRepository valuePropositionCanvasRepository,
                         AmbienteCanvasMapper ambienteCanvasMapper,
                         BusinessModelCanvasMapper businessModelCanvasMapper,
                         ChannelImplementationCanvasMapper channelImplementationCanvasMapper,
                         CustomerPersonasCanvasMapper customerPersonasCanvasMapper,
                         LeanCanvasMapper leanCanvasMapper,
                         ValuePropositionCanvasMapper valuePropositionCanvasMapper) {
        this.ambienteCanvasRepository = ambienteCanvasRepository;
        this.businessModelCanvasRepository = businessModelCanvasRepository;
        this.channelImplementationCanvasRepository = channelImplementationCanvasRepository;
        this.customerPersonasCanvasRepository = customerPersonasCanvasRepository;
        this.leanCanvasRepository = leanCanvasRepository;
        this.valuePropositionCanvasRepository = valuePropositionCanvasRepository;
        this.ambienteCanvasMapper = ambienteCanvasMapper;
        this.businessModelCanvasMapper = businessModelCanvasMapper;
        this.channelImplementationCanvasMapper = channelImplementationCanvasMapper;
        this.customerPersonasCanvasMapper = customerPersonasCanvasMapper;
        this.leanCanvasMapper = leanCanvasMapper;
        this.valuePropositionCanvasMapper = valuePropositionCanvasMapper;
    }

    /*
     *
     * Ambiente Canvas
     *
     */

    public List<AmbienteCanvasDTO> findAmbienteCanvasByIncCod(Long incCod){
        return ambienteCanvasMapper.toDTOList(ambienteCanvasRepository.findAmbienteCanvasByIncCod(incCod));
    }

    public AmbienteCanvasDTO findAmbienteCanvasById(Long ambcCod){
        return ambienteCanvasMapper.toDTO(ambienteCanvasRepository.findById(ambcCod).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @Transactional(rollbackFor = Exception.class)
    public AmbienteCanvasDTO saveAmbienteCanvas(AmbienteCanvasDTO ambienteCanvasDTO, Long ambcCod){
        return ambienteCanvasMapper.toDTO(ambienteCanvasRepository.save(ambienteCanvasMapper.toModel(ambienteCanvasDTO, ambcCod)));
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteAmbienteCanvas(Long ambcCod){

        businessModelCanvasRepository.deleteByAmbienteCanvasModel_AmbcCod(ambcCod);
        channelImplementationCanvasRepository.deleteByAmbienteCanvasModel_AmbcCod(ambcCod);
        customerPersonasCanvasRepository.deleteByAmbienteCanvasModel_AmbcCod(ambcCod);
        leanCanvasRepository.deleteByAmbienteCanvasModel_AmbcCod(ambcCod);
        valuePropositionCanvasRepository.deleteByAmbienteCanvasModel_AmbcCod(ambcCod);

        ambienteCanvasRepository.deleteById(ambcCod);
    }

    /*
    *
    * Business Model Canvas
    *
    */

    public BusinessModelCanvasDTO findByBusinessModelCanvasByAmbcCod(Long ambcCod){
        return businessModelCanvasMapper.toDTO(businessModelCanvasRepository
                .findByAmbienteCanvasModel_AmbcCod(ambcCod).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @Transactional(rollbackFor = Exception.class)
    public BusinessModelCanvasDTO saveBusinessModelCanvas(BusinessModelCanvasDTO businessModelCanvasDTO, Long ambcCod){
        Long bmcCod = businessModelCanvasDTO.bmcCod() != null
                ? businessModelCanvasDTO.bmcCod()
                : businessModelCanvasRepository.findByAmbienteCanvasModel_AmbcCod(ambcCod)
                    .map(BusinessModelCanvasModel::getBmcCod).orElse(null);
        return businessModelCanvasMapper.toDTO(businessModelCanvasRepository.
                save(businessModelCanvasMapper.toModel(businessModelCanvasDTO, bmcCod, ambcCod)));
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteBusinessModelCanvas(Long ambcCod){
        businessModelCanvasRepository.findByAmbienteCanvasModel_AmbcCod(ambcCod)
                .ifPresent(businessModelCanvasRepository::delete);
    }

    /*
    *
    * Channel Implementation Canvas
    *
    */

    public List<ChannelImplementationCanvasDTO> findChannelImplementationCanvasByAmbcCod(Long ambcCod){
        return channelImplementationCanvasMapper.toDTOList(channelImplementationCanvasRepository.findByAmbienteCanvasModel_AmbcCod(ambcCod));
    }

    public ChannelImplementationCanvasDTO findChannelImplementationCanvasById(Long segCod){
        return channelImplementationCanvasMapper.toDTO(channelImplementationCanvasRepository
                .findById(segCod).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @Transactional(rollbackFor = Exception.class)
    public ChannelImplementationCanvasDTO saveChannelImplementationCanvas(ChannelImplementationCanvasDTO channelImplementationCanvasDTO, Long segCod, Long ambcCod){
        return channelImplementationCanvasMapper.toDTO(channelImplementationCanvasRepository.save(channelImplementationCanvasMapper.toModel(channelImplementationCanvasDTO, segCod, ambcCod)));
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteChannelImplementationCanvas(Long segCod){
        channelImplementationCanvasRepository.deleteById(segCod);
    }

    /*
    *
    * Customer Persona Canvas
    *
    */

    public List<CustomerPersonasCanvasDTO> findCustomerPersonasByAmbcCod(Long ambcCod){
        return customerPersonasCanvasMapper.toDTOList(customerPersonasCanvasRepository.findByAmbienteCanvasModel_AmbcCod(ambcCod));
    }

    public CustomerPersonasCanvasDTO findCustomerPersonasCanvasById(Long personaCod){
        return customerPersonasCanvasMapper.toDTO(customerPersonasCanvasRepository
                .findById(personaCod).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @Transactional(rollbackFor = Exception.class)
    public CustomerPersonasCanvasDTO saveCustomerPersonasCanvas(CustomerPersonasCanvasDTO customerPersonasCanvasDTO, Long personaCod, Long ambcCod){
        return customerPersonasCanvasMapper.toDTO(customerPersonasCanvasRepository.save(customerPersonasCanvasMapper.toModel(customerPersonasCanvasDTO, personaCod, ambcCod)));
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteCustomerPersonasCanvas(Long personaCod){
        customerPersonasCanvasRepository.deleteById(personaCod);
    }

    /*
    *
    * Lean Canvas
    *
    */

    public LeanCanvasDTO findLeanCanvasByAmbcCod(Long ambcCod){
        return leanCanvasMapper.toDTO(leanCanvasRepository
                .findByAmbienteCanvasModel_AmbcCod(ambcCod).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @Transactional(rollbackFor = Exception.class)
    public LeanCanvasDTO saveLeanCanvas(LeanCanvasDTO leanCanvasDTO, Long ambcCod){
        Long leanCod = leanCanvasDTO.leanCod() != null
                ? leanCanvasDTO.leanCod()
                : leanCanvasRepository.findByAmbienteCanvasModel_AmbcCod(ambcCod)
                    .map(LeanCanvasModel::getLeanCod).orElse(null);
        return leanCanvasMapper.toDTO(leanCanvasRepository.save(leanCanvasMapper.toModel(leanCanvasDTO, leanCod, ambcCod)));
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteLeanCanvas(Long ambcCod){
        leanCanvasRepository.findByAmbienteCanvasModel_AmbcCod(ambcCod)
                .ifPresent(leanCanvasRepository::delete);
    }

    /*
    *
    * Value Proposition Canvas
    *
    */

    public ValuePropositionCanvasDTO findValuePropositionCanvasByAmbcCod(Long ambcCod){
        return valuePropositionCanvasMapper.toDTO(valuePropositionCanvasRepository.findByAmbienteCanvasModel_AmbcCod(ambcCod)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @Transactional(rollbackFor = Exception.class)
    public ValuePropositionCanvasDTO saveValuePropositionCanvas(ValuePropositionCanvasDTO valuePropositionCanvasDTO, Long ambcCod){
        Long vpcCod = valuePropositionCanvasDTO.vpcCod() != null
                ? valuePropositionCanvasDTO.vpcCod()
                : valuePropositionCanvasRepository.findByAmbienteCanvasModel_AmbcCod(ambcCod)
                    .map(ValuePropostionCanvasModel::getVpcCod).orElse(null);
        return valuePropositionCanvasMapper.toDTO(valuePropositionCanvasRepository
                .save(valuePropositionCanvasMapper.toModel(valuePropositionCanvasDTO, vpcCod, ambcCod)));
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteValuePropositionCanvas(Long ambcCod){
        valuePropositionCanvasRepository.findByAmbienteCanvasModel_AmbcCod(ambcCod)
                .ifPresent(valuePropositionCanvasRepository::delete);
    }
}




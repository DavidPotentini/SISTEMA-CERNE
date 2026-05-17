package com.github.davidpotentini.cerne2.service.planejamentoestrategico;

import com.github.davidpotentini.cerne2.dto.planejamentoestrategico.EvidenciasDTO;
import com.github.davidpotentini.cerne2.dto.planejamentoestrategico.ObjetivosDTO;
import com.github.davidpotentini.cerne2.dto.planejamentoestrategico.PlanejamentoEstrategicoDTO;
import com.github.davidpotentini.cerne2.dto.planejamentoestrategico.ProjetosDTO;
import com.github.davidpotentini.cerne2.dto.planejamentoestrategico.TarefasDTO;
import com.github.davidpotentini.cerne2.mapper.planejamentoestrategico.EvidenciasMapper;
import com.github.davidpotentini.cerne2.mapper.planejamentoestrategico.ObjetivosMapper;
import com.github.davidpotentini.cerne2.mapper.planejamentoestrategico.PlanejamentoEstrategicoMapper;
import com.github.davidpotentini.cerne2.mapper.planejamentoestrategico.ProjetosMapper;
import com.github.davidpotentini.cerne2.mapper.planejamentoestrategico.TarefasMapper;
import com.github.davidpotentini.cerne2.models.planejamentoestrategico.ObjetivosModel;
import com.github.davidpotentini.cerne2.models.planejamentoestrategico.ProjetosModel;
import com.github.davidpotentini.cerne2.models.planejamentoestrategico.TarefasModel;
import com.github.davidpotentini.cerne2.repository.planejamentoestrategico.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class PlanejamentoEstrategicoService {

    private final PlanejamentoEstrategicoRepository planejamentoEstrategicoRepository;
    private final ProjetosRepository projetosRepository;
    private final ObjetivosRepository objetivosRepository;
    private final TarefasRepository tarefasRepository;
    private final EvidenciasRepository evidenciasRepository;
    private final PlanejamentoEstrategicoMapper planejamentoEstrategicoMapper;
    private final ProjetosMapper projetosMapper;
    private final ObjetivosMapper objetivosMapper;
    private final TarefasMapper tarefasMapper;
    private final EvidenciasMapper evidenciasMapper;

    public PlanejamentoEstrategicoService(PlanejamentoEstrategicoRepository planejamentoEstrategicoRepository,
                                          ProjetosRepository projetosRepository,
                                          ObjetivosRepository objetivosRepository,
                                          TarefasRepository tarefasRepository,
                                          EvidenciasRepository evidenciasRepository,
                                          PlanejamentoEstrategicoMapper planejamentoEstrategicoMapper,
                                          ProjetosMapper projetosMapper,
                                          ObjetivosMapper objetivosMapper,
                                          TarefasMapper tarefasMapper,
                                          EvidenciasMapper evidenciasMapper) {
        this.planejamentoEstrategicoRepository = planejamentoEstrategicoRepository;
        this.projetosRepository = projetosRepository;
        this.objetivosRepository = objetivosRepository;
        this.tarefasRepository = tarefasRepository;
        this.evidenciasRepository = evidenciasRepository;
        this.planejamentoEstrategicoMapper = planejamentoEstrategicoMapper;
        this.projetosMapper = projetosMapper;
        this.objetivosMapper = objetivosMapper;
        this.tarefasMapper = tarefasMapper;
        this.evidenciasMapper = evidenciasMapper;
    }

    /*
     *
     * Planos Anuais Integrados
     *
     */

    public List<PlanejamentoEstrategicoDTO> findList(){
        return planejamentoEstrategicoMapper.toDTOList(planejamentoEstrategicoRepository.findAll());
    }

    public PlanejamentoEstrategicoDTO findById (Long pesCod){
        return planejamentoEstrategicoMapper.toDTO(planejamentoEstrategicoRepository.findById(pesCod)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @Transactional(rollbackFor = Exception.class)
    public PlanejamentoEstrategicoDTO save (PlanejamentoEstrategicoDTO planejamentoEstrategicoDTO, Long pesCod){
        return planejamentoEstrategicoMapper.toDTO(planejamentoEstrategicoRepository
                .save(planejamentoEstrategicoMapper.toModel(planejamentoEstrategicoDTO, pesCod)));
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete (Long pesCod) {

        List<ProjetosModel> projetosModelList = projetosRepository.findByPlanejamentoEstrategicoModel_PesCod(pesCod);

        for (ProjetosModel p: projetosModelList){
            deleteProjetos(p.getPrjCod());
        }

        planejamentoEstrategicoRepository.deleteById(pesCod);
    }

    /*
     *
     * Projetos
     *
     */

    public List<ProjetosDTO> findProjetoByPlano(Long pesCod) {
        return projetosMapper.toDTOList(projetosRepository.findByPlanejamentoEstrategicoModel_PesCod(pesCod));
    }

    public ProjetosDTO findByProjetoId(Long prjCod) {
        return projetosMapper.toDTO(projetosRepository.findById(prjCod)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @Transactional(rollbackFor = Exception.class)
    public ProjetosDTO saveProjetos(ProjetosDTO projetosDTO, Long pesCod, Long prjCod) {
        return projetosMapper.toDTO(projetosRepository.save(projetosMapper.toModel(projetosDTO, prjCod, pesCod)));
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteProjetos(Long prjCod) {

        List<ObjetivosModel> objetivosModelList = objetivosRepository.findByProjetosModel_PrjCod(prjCod);

        for (ObjetivosModel o: objetivosModelList){
            deleteObjetivos(o.getObjCod());
        }

        projetosRepository.deleteById(prjCod);
    }

    /*
     *
     * Objetivos
     *
     */

    public List<ObjetivosDTO> findObjetivosByProjetos(Long prjCod){
        return objetivosMapper.toDTOList(objetivosRepository.findByProjetosModel_PrjCod(prjCod));
    }

    public ObjetivosDTO findByObjetivosId(Long objCod){
        return objetivosMapper.toDTO(objetivosRepository.findById(objCod)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @Transactional(rollbackFor = Exception.class)
    public ObjetivosDTO saveObjetivos(ObjetivosDTO objetivosDTO, Long prjCod, Long objCod){
        return objetivosMapper.toDTO(objetivosRepository.save(objetivosMapper.toModel(objetivosDTO, objCod, prjCod)));
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteObjetivos(Long objCod){

        List<TarefasModel> tarefasModelList = tarefasRepository.findByObjetivosModel_ObjCod(objCod);

        for (TarefasModel t: tarefasModelList){
            deleteTarefas(t.getTrfCod());
        }

        objetivosRepository.deleteById(objCod);
    }

    /*
     *
     * Tarefas
     *
     */

    public List<TarefasDTO> findTarefasByObjetivos(Long objCod){
        return tarefasMapper.toDTOList(tarefasRepository.findByObjetivosModel_ObjCod(objCod));
    }

    public TarefasDTO findByTarefaId(Long trfCod){
        return tarefasMapper.toDTO(tarefasRepository.findById(trfCod)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @Transactional(rollbackFor = Exception.class)
    public TarefasDTO saveTarefas(TarefasDTO tarefasDTO, Long objCod, Long trfCod){
        return tarefasMapper.toDTO(tarefasRepository.save(tarefasMapper.toModel(tarefasDTO, trfCod, objCod)));
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteTarefas(Long trfCod){
        evidenciasRepository.deleteByTarefasModel_TrfCod(trfCod);
        tarefasRepository.deleteById(trfCod);
    }

    /*
     *
     * Evidências
     *
     */

    public List<EvidenciasDTO> findEvidenciasByTarefas(Long trfCod){
        return evidenciasMapper.toDTOList(evidenciasRepository.findByTarefasModel_TrfCod(trfCod)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    public EvidenciasDTO findByEvidenciaId(Long evdCod){
        return evidenciasMapper.toDTO(evidenciasRepository.findById(evdCod)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @Transactional(rollbackFor = Exception.class)
    public EvidenciasDTO saveEvidencia(EvidenciasDTO evidenciasDTO, Long evdCod){
        return evidenciasMapper.toDTO(evidenciasRepository.save(evidenciasMapper.toModel(evidenciasDTO, evdCod)));
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteEvidencia(Long evdCod){
        evidenciasRepository.deleteById(evdCod);
    }
}

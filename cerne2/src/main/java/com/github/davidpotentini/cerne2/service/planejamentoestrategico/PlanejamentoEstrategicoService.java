package com.github.davidpotentini.cerne2.service.planejamentoestrategico;

import com.github.davidpotentini.cerne2.dto.planejamentoestrategico.request.*;
import com.github.davidpotentini.cerne2.dto.planejamentoestrategico.response.*;
import com.github.davidpotentini.cerne2.models.planejamentoestrategico.*;
import com.github.davidpotentini.cerne2.repository.pessoas.PessoasRepository;
import com.github.davidpotentini.cerne2.repository.planejamentoestrategico.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class PlanejamentoEstrategicoService {

    private final PlanejamentoEstrategicoRepository planejamentoEstrategicoRepository;
    private final ProjetosRepository projetosRepository;
    private final ObjetivosRepository objetivosRepository;
    private final TarefasRepository tarefasRepository;
    private final EvidenciasRepository evidenciasRepository;
    private final PessoasRepository pessoasRepository;

    public PlanejamentoEstrategicoService(PlanejamentoEstrategicoRepository planejamentoEstrategicoRepository,
                                          ProjetosRepository projetosRepository,
                                          ObjetivosRepository objetivosRepository,
                                          TarefasRepository tarefasRepository,
                                          EvidenciasRepository evidenciasRepository,
                                          PessoasRepository pessoasRepository) {

        this.planejamentoEstrategicoRepository = planejamentoEstrategicoRepository;
        this.projetosRepository = projetosRepository;
        this.objetivosRepository = objetivosRepository;
        this.tarefasRepository = tarefasRepository;
        this.evidenciasRepository = evidenciasRepository;
        this.pessoasRepository = pessoasRepository;
    }

    /*
    *
    * Planos Anuais Integrados
    *
    */

    public  List<PlanejamentoEstrategicoDTOResponse> findList(){
        List<PlanejamentoEstrategicoModel> planejamentoEstrategicoModelList = planejamentoEstrategicoRepository.findAll();

        List<PlanejamentoEstrategicoDTOResponse> planejamentoEstrategicoDTORespons = new ArrayList<>();

        for (PlanejamentoEstrategicoModel planejamentoEstrategicoModel : planejamentoEstrategicoModelList){
                planejamentoEstrategicoDTORespons.add(mapToPlanoAnualIntegradoDTO(planejamentoEstrategicoModel));
        }

        return planejamentoEstrategicoDTORespons;
    }

    public PlanejamentoEstrategicoDTOResponse findById (Long pesCod){
        return mapToPlanoAnualIntegradoDTO(planejamentoEstrategicoRepository.findById(pesCod).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @Transactional(rollbackFor = Exception.class)
    public PlanejamentoEstrategicoDTOResponse save (PlanejamentoEstrategicoDTORequest planejamentoEstrategicoDTORequest, Long pesCod){
        return mapToPlanoAnualIntegradoDTO(planejamentoEstrategicoRepository.save(mapToPlanosAnuaisIntegradosModel(planejamentoEstrategicoDTORequest, pesCod)));
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete (Long pesCod){
         planejamentoEstrategicoRepository.delete(planejamentoEstrategicoRepository.findById(pesCod)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    /*
     *
     * Projetos
     *
     */

    public List<ProjetosDTOResponse> findProjetoByPlano(Long pesCod) {
        List<ProjetosModel> projetosModelList =  projetosRepository.findByPlanejamentoEstrategicoModel_PesCod(pesCod)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<ProjetosDTOResponse> projetosDTOResponses = new ArrayList<>();

        for (ProjetosModel projetosModel: projetosModelList) {
            projetosDTOResponses.add(mapToProjetosDTO(projetosModel));
        }

        return projetosDTOResponses;
    }

    public ProjetosDTOResponse findByProjetoId(Long prjCod) {
        ProjetosModel projetosModel = projetosRepository.findById(prjCod)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return mapToProjetosDTO(projetosModel);
    }

    @Transactional(rollbackFor = Exception.class)
    public ProjetosDTOResponse saveProjetos(ProjetosDTORequest projetosDTORequest, Long pesCod, Long prjCod) {
        return mapToProjetosDTO(projetosRepository.save(mapToProjetosModel(projetosDTORequest, pesCod, prjCod)));
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteProjetos(Long prjCod) {
        projetosRepository.delete(projetosRepository.findById(prjCod)
                .orElseThrow(() -> new ResponseStatusException (HttpStatus.NOT_FOUND)));
    }


    /*
     *
     * Objetivos
     *
     */


    public List<ObjetivosDTOResponse> findObjetivosByProjetos(Long prjCod){
        List<ObjetivosModel> objetivosModelList = objetivosRepository.findByProjetosModel_PrjCod(prjCod)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<ObjetivosDTOResponse> objetivosDTOResponses = new ArrayList<>();

        for (ObjetivosModel o : objetivosModelList){
            objetivosDTOResponses.add(mapToObjetivosDTO(o));
        }

        return objetivosDTOResponses;
    }

    public ObjetivosDTOResponse findByObjetivosId(Long objCod){
        ObjetivosModel objetivosModel = objetivosRepository.findById(objCod)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return mapToObjetivosDTO(objetivosModel);
    }

    @Transactional(rollbackFor = Exception.class)
    public ObjetivosDTOResponse saveObjetivos(ObjetivosDTORequest objetivosDTORequest, Long prjCod, Long objCod){
        return mapToObjetivosDTO(objetivosRepository.save(mapToObjetivosModel(objetivosDTORequest, prjCod, objCod)));
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteObjetivos(Long objCod){
        objetivosRepository.delete(objetivosRepository
                .findById(objCod).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    /*
    *
    *Tarefas
    *
    */

    public List<TarefasDTOResponse> findTarefasByObjetivos(Long objCod){
        List<TarefasModel> tarefasModelList = tarefasRepository.findByObjetivosModel_ObjCod(objCod)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<TarefasDTOResponse> tarefasDTOResponses = new ArrayList<>();

        for(TarefasModel t: tarefasModelList){
            tarefasDTOResponses.add(mapToTarefasDTO(t));
        }

        return tarefasDTOResponses;
    }

    public TarefasDTOResponse findByTarefaId(Long trfCod){
        TarefasModel tarefasModel = tarefasRepository.findById(trfCod)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return mapToTarefasDTO(tarefasModel);
    }

    @Transactional(rollbackFor = Exception.class)
    public TarefasDTOResponse saveTarefas(TarefasDTORequest tarefasDTORequest, Long objCod, Long trfCod){
        return mapToTarefasDTO(tarefasRepository.save(mapToTarefasModel(tarefasDTORequest, objCod, trfCod)));
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteTarefas(Long trfCod){
        tarefasRepository.delete(tarefasRepository.findById(trfCod)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    /*
    *
    * Evidências
    *
    */

    public List<EvidenciasDTOResponse> findEvidenciasByTarefas(Long trfCod){
        List<EvidenciasModel> evidenciasModelList = evidenciasRepository.findByTarefasModel_TrfCod(trfCod)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<EvidenciasDTOResponse> evidenciasDTOS = new ArrayList<>();

        for (EvidenciasModel e : evidenciasModelList){
            evidenciasDTOS.add(mapToEvidenciasDTO(e));
        }

        return evidenciasDTOS;
    }

    public EvidenciasDTOResponse findByEvidenciaId(Long evdCod){
        EvidenciasModel evidenciasModel = evidenciasRepository.findById(evdCod)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return mapToEvidenciasDTO(evidenciasModel);
    }

    public EvidenciasDTOResponse saveEvidencia(EvidenciasDTORequest evidenciasDTORequest, Long evdCod){
        return mapToEvidenciasDTO(evidenciasRepository.save(mapToEvidenciasModel(evidenciasDTORequest, evdCod)));
    }

    public void deleteEvidencia(Long evdCod){
        evidenciasRepository.deleteById(evdCod);
    }

    /*
    *
    * Model para DTO, e vice-versa
    *
    */

    private PlanejamentoEstrategicoDTOResponse mapToPlanoAnualIntegradoDTO(PlanejamentoEstrategicoModel planejamentoEstrategicoModel){
        return new PlanejamentoEstrategicoDTOResponse(planejamentoEstrategicoModel.getPesCod(),
                                             planejamentoEstrategicoModel.getNome(),
                                             planejamentoEstrategicoModel.getDataInicio(),
                                             planejamentoEstrategicoModel.getDataTermino());
    }

    public PlanejamentoEstrategicoModel mapToPlanosAnuaisIntegradosModel(PlanejamentoEstrategicoDTORequest planejamentoEstrategicoDTORequest, Long pesCod){
        PlanejamentoEstrategicoModel planejamentoEstrategicoModel = new PlanejamentoEstrategicoModel();

        planejamentoEstrategicoModel.setPesCod(pesCod);
        planejamentoEstrategicoModel.setNome(planejamentoEstrategicoDTORequest.nome());
        planejamentoEstrategicoModel.setDataInicio(planejamentoEstrategicoDTORequest.dataInicio());
        planejamentoEstrategicoModel.setDataTermino(planejamentoEstrategicoDTORequest.dataTermino());

        return planejamentoEstrategicoModel;
    }

    private ProjetosDTOResponse mapToProjetosDTO(ProjetosModel projetosModel){
        return new ProjetosDTOResponse(projetosModel.getPrjCod(),
                               projetosModel.getNome(),
                               projetosModel.getDataInicio(),
                               projetosModel.getDataTermino(),
                               projetosModel.getPlanejamentoEstrategicoModel().getPesCod());
    }

    public ProjetosModel mapToProjetosModel(ProjetosDTORequest projetosDTORequest, Long pesCod, Long prjCod) {
        ProjetosModel projetosModel = new ProjetosModel();

        projetosModel.setPrjCod(prjCod);
        projetosModel.setNome(projetosDTORequest.nome());
        projetosModel.setDataInicio(projetosDTORequest.dataInicio());
        projetosModel.setDataTermino(projetosDTORequest.dataTermino());

        projetosModel.setPlanejamentoEstrategicoModel(planejamentoEstrategicoRepository
                .findById(pesCod).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));

        return projetosModel;
    }

    public ObjetivosDTOResponse mapToObjetivosDTO (ObjetivosModel objetivosModel){
        return new ObjetivosDTOResponse(objetivosModel.getObjCod(),
                                objetivosModel.getNome(),
                                objetivosModel.getDataInicio(),
                                objetivosModel.getDataTermino(),
                                objetivosModel.getProjetosModel().getPrjCod());
    }

    public ObjetivosModel mapToObjetivosModel(ObjetivosDTORequest objetivosDTORequest, Long prjCod, Long objCod){
        ObjetivosModel objetivosModel = new ObjetivosModel();

        objetivosModel.setObjCod(objCod);
        objetivosModel.setNome(objetivosDTORequest.nome());
        objetivosModel.setDataInicio(objetivosDTORequest.dataInicio());
        objetivosModel.setDataTermino(objetivosDTORequest.dataTermino());

        objetivosModel.setProjetosModel(projetosRepository
                .findById(prjCod).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));

        return objetivosModel;
    }

    public TarefasDTOResponse mapToTarefasDTO (TarefasModel tarefasModel){
        return new TarefasDTOResponse(tarefasModel.getTrfCod(),
                              tarefasModel.getNome(),
                              tarefasModel.getDataInicio(),
                              tarefasModel.getDataTermino(),
                              tarefasModel.getESituacaoTarefa(),
                              tarefasModel.getObjetivosModel().getObjCod(),
                              tarefasModel.getPessoasModel().getPessoaCod());
    }

    public TarefasModel mapToTarefasModel (TarefasDTORequest tarefasDTORequest, Long objCod, Long trfCod){
        TarefasModel tarefasModel = new TarefasModel();

        tarefasModel.setTrfCod(trfCod);
        tarefasModel.setNome(tarefasDTORequest.nome());
        tarefasModel.setDataInicio(tarefasDTORequest.dataInicio());
        tarefasModel.setDataTermino(tarefasDTORequest.dataTermino());
        tarefasModel.setESituacaoTarefa(tarefasDTORequest.eSituacaoTarefa());

        tarefasModel.setObjetivosModel(objetivosRepository.findById(objCod)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));

        tarefasModel.setPessoasModel(pessoasRepository.findById(tarefasDTORequest.respCod())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));

        return tarefasModel;
    }

        public EvidenciasDTOResponse mapToEvidenciasDTO (EvidenciasModel evidenciasModel){
        return new EvidenciasDTOResponse(evidenciasModel.getEvdCod(),
                                 evidenciasModel.getDescricao(),
                                 evidenciasModel.getCaminhoArquivo(),
                                 evidenciasModel.getTarefasModel().getTrfCod());
    }

    public EvidenciasModel mapToEvidenciasModel (EvidenciasDTORequest evidenciasDTORequest, Long evdCod){
        EvidenciasModel evidenciasModel = new EvidenciasModel();

        evidenciasModel.setEvdCod(evdCod);
        evidenciasModel.setDescricao(evidenciasDTORequest.descricao());
        evidenciasModel.setCaminhoArquivo(evidenciasDTORequest.caminhoArquivo());

        evidenciasModel.setTarefasModel(tarefasRepository.findById(evidenciasDTORequest.trfCod())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));

        return evidenciasModel;
    }
}

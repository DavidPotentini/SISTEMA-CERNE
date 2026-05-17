package com.github.davidpotentini.cerne2.controllers.planejamentoestrategico;

import com.github.davidpotentini.cerne2.dto.planejamentoestrategico.*;
import com.github.davidpotentini.cerne2.service.planejamentoestrategico.PlanejamentoEstrategicoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/planosAnuaisIntegrados")
public class PlanejamentoEstrategicoController {

    private final PlanejamentoEstrategicoService planejamentoEstrategicoService;

    public PlanejamentoEstrategicoController(PlanejamentoEstrategicoService planejamentoEstrategicoService) {
        this.planejamentoEstrategicoService = planejamentoEstrategicoService;
    }

    /*
    *
    * Planos Anuais Integrados
    *
    */

    @GetMapping
    public ResponseEntity<List<PlanejamentoEstrategicoDTO>> findList(){
        List<PlanejamentoEstrategicoDTO> planejamentoEstrategicoDTORespons = planejamentoEstrategicoService.findList();

        return ResponseEntity.ok(planejamentoEstrategicoDTORespons);
    }

    @GetMapping("/{pesCod}")
    public ResponseEntity<PlanejamentoEstrategicoDTO> findById(@PathVariable Long pesCod){
        PlanejamentoEstrategicoDTO planejamentoEstrategicoDTOResponse = planejamentoEstrategicoService.findById(pesCod);

        return ResponseEntity.ok(planejamentoEstrategicoDTOResponse);
    }

    @PostMapping
    public ResponseEntity<PlanejamentoEstrategicoDTO> insert(@RequestBody PlanejamentoEstrategicoDTO planejamentoEstrategicoDTORequest){
        PlanejamentoEstrategicoDTO planosDTO = planejamentoEstrategicoService.save(planejamentoEstrategicoDTORequest, null);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{pesCod}")
                .buildAndExpand(planosDTO.pesCod())
                .toUri();

        return ResponseEntity.created(location).body(planosDTO);
    }

    @PutMapping("/{pesCod}")
    public ResponseEntity<PlanejamentoEstrategicoDTO> update(@RequestBody PlanejamentoEstrategicoDTO planejamentoEstrategicoDTORequest,
                                                                     @PathVariable Long pesCod) {

        PlanejamentoEstrategicoDTO planosDTO = planejamentoEstrategicoService.save(planejamentoEstrategicoDTORequest, pesCod);

        return ResponseEntity.ok(planosDTO);
    }

    @DeleteMapping("/{pesCod}")
    public ResponseEntity<Void> delete(@PathVariable Long pesCod) {
        planejamentoEstrategicoService.delete(pesCod);

        return ResponseEntity.noContent().build();
    }

    /*
     *
     * Projetos
     *
     */

    @GetMapping("{pesCod}/projetos")
    public ResponseEntity<List<ProjetosDTO>> findProjetoByPlano (@PathVariable Long pesCod) {
        List<ProjetosDTO> projetosDTOResponseList = planejamentoEstrategicoService.findProjetoByPlano(pesCod);

        return ResponseEntity.ok(projetosDTOResponseList);
    }

    @GetMapping("{pesCod}/projetos/{prjCod}")
    public ResponseEntity<ProjetosDTO> findByProjetoId(@PathVariable Long prjCod){
        ProjetosDTO projetosDTOResponse = planejamentoEstrategicoService.findByProjetoId(prjCod);

        return ResponseEntity.ok(projetosDTOResponse);
    }

    @PostMapping("{pesCod}/projetos")
    public ResponseEntity<ProjetosDTO> insertProjetos(@RequestBody ProjetosDTO projetosDTORequest,
                                                              @PathVariable Long pesCod){
        ProjetosDTO projetosDTOResponse = planejamentoEstrategicoService.saveProjetos(projetosDTORequest, pesCod, null);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/prjCod")
                .buildAndExpand(projetosDTOResponse.prjCod())
                .toUri();

        return ResponseEntity.created(location).body(projetosDTOResponse);
    }

    @PutMapping("{pesCod}/projetos/{prjCod}")
    public ResponseEntity<ProjetosDTO> updateProjetos(@RequestBody ProjetosDTO projetosDTORequest,
                                                              @PathVariable Long pesCod,
                                                              @PathVariable Long prjCod){
        ProjetosDTO projetosDTOResponse = planejamentoEstrategicoService.saveProjetos(projetosDTORequest, pesCod, prjCod);

        return ResponseEntity.ok(projetosDTOResponse);
    }

    @DeleteMapping("{pesCod}/projetos/{prjCod}")
    public ResponseEntity<Void> deleteProjetos (@PathVariable Long prjCod) {
        planejamentoEstrategicoService.deleteProjetos(prjCod);

        return ResponseEntity.noContent().build();
    }

    /*
     *
     * Objetivos
     *
     */

    @GetMapping("{pesCod}/projetos/{prjCod}/objetivos")
    public ResponseEntity<List<ObjetivosDTO>> findObjetivosByProjetos(@PathVariable Long prjCod){
        List<ObjetivosDTO> objetivosDTOResponse = planejamentoEstrategicoService.findObjetivosByProjetos(prjCod);

        return ResponseEntity.ok(objetivosDTOResponse);
    }

    @GetMapping("{pesCod}/projetos/{prjCod}/objetivos/{objCod}")
    public ResponseEntity<ObjetivosDTO> findByObjetivosId(@PathVariable Long objCod){
        ObjetivosDTO objetivosDTOResponse = planejamentoEstrategicoService.findByObjetivosId(objCod);

        return ResponseEntity.ok(objetivosDTOResponse);
    }

    @PostMapping("{pesCod}/projetos/{prjCod}/objetivos")
    public ResponseEntity<ObjetivosDTO> insertObjetivos(@RequestBody ObjetivosDTO ObjetivosDTO,
                                                                @PathVariable Long prjCod){
        ObjetivosDTO objetivosDTOResponse = planejamentoEstrategicoService.saveObjetivos(ObjetivosDTO,
                                                                                                prjCod,
                                                                                                null);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("{objCod}")
                .buildAndExpand(objetivosDTOResponse.objCod())
                .toUri();

        return ResponseEntity.created(location).body(objetivosDTOResponse);
    }

    @PutMapping("{pesCod}/projetos/{prjCod}/objetivos/{objCod}")
    public ResponseEntity<ObjetivosDTO> updateObjetivos(@RequestBody ObjetivosDTO objetivosDTORequest,
                                                                @PathVariable Long prjCod,
                                                                @PathVariable Long objCod){
        ObjetivosDTO objetivosDTOResponse = planejamentoEstrategicoService.saveObjetivos(objetivosDTORequest,
                                                                                                 prjCod,
                                                                                                 objCod);

        return ResponseEntity.ok(objetivosDTOResponse);
    }

    @DeleteMapping("{pesCod}/projetos/{prjCod}/objetivos/{objCod}")
    public ResponseEntity<Void> deleteObjetivos(@PathVariable Long objCod){
        planejamentoEstrategicoService.deleteObjetivos(objCod);

        return ResponseEntity.noContent().build();
    }

    /*
    *
    * Tarefas
    *
    */

    @GetMapping("{pesCod}/projetos/{prjCod}/objetivos/{objCod}/tarefas")
    public ResponseEntity<List<TarefasDTO>> findTarefasByObjetivos(@PathVariable Long objCod){
        List<TarefasDTO> tarefasDTOResponse = planejamentoEstrategicoService.findTarefasByObjetivos(objCod);

        return ResponseEntity.ok(tarefasDTOResponse);
    }

    @GetMapping("{pesCod}/projetos/{prjCod}/objetivos/{objCod}/tarefas/{trfCod}")
    public ResponseEntity<TarefasDTO> findByTarefaId(@PathVariable Long trfCod){
        TarefasDTO tarefasDTOResponse = planejamentoEstrategicoService.findByTarefaId(trfCod);

        return ResponseEntity.ok(tarefasDTOResponse);
    }

    @PostMapping("{pesCod}/projetos/{prjCod}/objetivos/{objCod}/tarefas")
    public ResponseEntity<TarefasDTO> insertTarefas(@RequestBody TarefasDTO tarefasDTORequest,
                                                            @PathVariable Long objCod){
        TarefasDTO tarefasDTOResponse = planejamentoEstrategicoService.saveTarefas(tarefasDTORequest,
                                                                                           objCod,
                                                                                           null);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/trfCod")
                .buildAndExpand(tarefasDTOResponse.trfCod())
                .toUri();

        return ResponseEntity.created(location).body(tarefasDTOResponse);
    }

    @PutMapping("{pesCod}/projetos/{prjCod}/objetivos/{objCod}/tarefas/{trfCod}")
    public ResponseEntity<TarefasDTO> updateTarefas(@RequestBody TarefasDTO tarefasDTORequest,
                                                            @PathVariable Long objCod,
                                                            @PathVariable Long trfCod){

        TarefasDTO tarefasDTOResponse = planejamentoEstrategicoService.saveTarefas(tarefasDTORequest,
                                                                                           objCod,
                                                                                           trfCod);

        return ResponseEntity.ok(tarefasDTOResponse);
    }

    @DeleteMapping("{pesCod}/projetos/{prjCod}/objetivos/{objCod}/tarefas/{trfCod}")
    public ResponseEntity<Void> deleteTarefas(@PathVariable Long trfCod){
        planejamentoEstrategicoService.deleteTarefas(trfCod);

        return ResponseEntity.noContent().build();
    }

    /*
    *
    * Evidencias
    *
    */

    @GetMapping("{pesCod}/projetos/{prjCod}/objetivos/{objCod}/tarefas/{trfCod}/evidencias")
    public ResponseEntity<List<EvidenciasDTO>> findEvidenciasByTarefas(@PathVariable Long trfCod){
        return ResponseEntity.ok(planejamentoEstrategicoService.findEvidenciasByTarefas(trfCod));
    }

    @PostMapping("{pesCod}/projetos/{prjCod}/objetivos/{objCod}/tarefas/{trfCod}/evidencias")
    public ResponseEntity<EvidenciasDTO> insertEvidencias(@RequestBody EvidenciasDTO evidenciasDTORequest){
        EvidenciasDTO evidenciasDTOResponse = planejamentoEstrategicoService.saveEvidencia(evidenciasDTORequest, null);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{evdCod}")
                .buildAndExpand(evidenciasDTOResponse.evdCod())
                .toUri();

        return ResponseEntity.created(location).body(evidenciasDTOResponse);
    }

    @PostMapping("{pesCod}/projetos/{prjCod}/objetivos/{objCod}/tarefas/{trfCod}/evidencias/{evdCod}")
    public ResponseEntity<EvidenciasDTO> updateEvidencias(@RequestBody EvidenciasDTO evidenciasDTORequest,
                                                                  @PathVariable Long evdCod){
        return ResponseEntity.ok(planejamentoEstrategicoService.saveEvidencia(evidenciasDTORequest, evdCod));
    }

    @DeleteMapping("{pesCod}/projetos/{prjCod}/objetivos/{objCod}/tarefas/{trfCod}/evidencias/{evdCod}")
    public ResponseEntity<Void> deleteEvidencias(@PathVariable Long evdCod){
        planejamentoEstrategicoService.deleteEvidencia(evdCod);

        return ResponseEntity.noContent().build();
    }
}


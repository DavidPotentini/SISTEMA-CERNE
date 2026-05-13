package com.github.davidpotentini.cerne2.controllers.planejamentoestrategico;

import com.github.davidpotentini.cerne2.dto.planejamentoestrategico.request.*;
import com.github.davidpotentini.cerne2.dto.planejamentoestrategico.response.*;
import com.github.davidpotentini.cerne2.service.planejamentoestrategico.PlanejamentoEstrategicoService;
import jakarta.persistence.Lob;
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
    public ResponseEntity<List<PlanejamentoEstrategicoDTOResponse>> findList(){
        List<PlanejamentoEstrategicoDTOResponse> planejamentoEstrategicoDTORespons = planejamentoEstrategicoService.findList();

        return ResponseEntity.ok(planejamentoEstrategicoDTORespons);
    }

    @GetMapping("/{pesCod}")
    public ResponseEntity<PlanejamentoEstrategicoDTOResponse> findById(@PathVariable Long pesCod){
        PlanejamentoEstrategicoDTOResponse planejamentoEstrategicoDTOResponse = planejamentoEstrategicoService.findById(pesCod);

        return ResponseEntity.ok(planejamentoEstrategicoDTOResponse);
    }

    @PostMapping
    public ResponseEntity<PlanejamentoEstrategicoDTOResponse> insert(@RequestBody PlanejamentoEstrategicoDTORequest planejamentoEstrategicoDTORequest){
        PlanejamentoEstrategicoDTOResponse planosDTO = planejamentoEstrategicoService.save(planejamentoEstrategicoDTORequest, null);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{pesCod}")
                .buildAndExpand(planosDTO.pesCod())
                .toUri();

        return ResponseEntity.created(location).body(planosDTO);
    }

    @PutMapping("/{pesCod}")
    public ResponseEntity<PlanejamentoEstrategicoDTOResponse> update(@RequestBody PlanejamentoEstrategicoDTORequest planejamentoEstrategicoDTORequest,
                                                                     @PathVariable Long pesCod) {

        PlanejamentoEstrategicoDTOResponse planosDTO = planejamentoEstrategicoService.save(planejamentoEstrategicoDTORequest, pesCod);

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
    public ResponseEntity<List<ProjetosDTOResponse>> findProjetoByPlano (@PathVariable Long pesCod) {
        List<ProjetosDTOResponse> projetosDTOResponseList = planejamentoEstrategicoService.findProjetoByPlano(pesCod);

        return ResponseEntity.ok(projetosDTOResponseList);
    }

    @GetMapping("{pesCod}/projetos/{prjCod}")
    public ResponseEntity<ProjetosDTOResponse> findByProjetoId(@PathVariable Long prjCod){
        ProjetosDTOResponse projetosDTOResponse = planejamentoEstrategicoService.findByProjetoId(prjCod);

        return ResponseEntity.ok(projetosDTOResponse);
    }

    @PostMapping("{pesCod}/projetos")
    public ResponseEntity<ProjetosDTOResponse> insertProjetos(@RequestBody ProjetosDTORequest projetosDTORequest,
                                                              @PathVariable Long pesCod){
        ProjetosDTOResponse projetosDTOResponse = planejamentoEstrategicoService.saveProjetos(projetosDTORequest, pesCod, null);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/prjCod")
                .buildAndExpand(projetosDTOResponse.prjCod())
                .toUri();

        return ResponseEntity.created(location).body(projetosDTOResponse);
    }

    @PutMapping("{pesCod}/projetos/{prjCod}")
    public ResponseEntity<ProjetosDTOResponse> updateProjetos(@RequestBody ProjetosDTORequest projetosDTORequest,
                                                              @PathVariable Long pesCod,
                                                              @PathVariable Long prjCod){
        ProjetosDTOResponse projetosDTOResponse = planejamentoEstrategicoService.saveProjetos(projetosDTORequest, pesCod, prjCod);

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
    public ResponseEntity<List<ObjetivosDTOResponse>> findObjetivosByProjetos(@PathVariable Long prjCod){
        List<ObjetivosDTOResponse> objetivosDTOResponse = planejamentoEstrategicoService.findObjetivosByProjetos(prjCod);

        return ResponseEntity.ok(objetivosDTOResponse);
    }

    @GetMapping("{pesCod}/projetos/{prjCod}/objetivos/{objCod}")
    public ResponseEntity<ObjetivosDTOResponse> findByObjetivosId(@PathVariable Long objCod){
        ObjetivosDTOResponse objetivosDTOResponse = planejamentoEstrategicoService.findByObjetivosId(objCod);

        return ResponseEntity.ok(objetivosDTOResponse);
    }

    @PostMapping("{pesCod}/projetos/{prjCod}/objetivos")
    public ResponseEntity<ObjetivosDTOResponse> insertObjetivos(@RequestBody ObjetivosDTORequest ObjetivosDTORequest,
                                                                @PathVariable Long prjCod){
        ObjetivosDTOResponse objetivosDTOResponse = planejamentoEstrategicoService.saveObjetivos(ObjetivosDTORequest,
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
    public ResponseEntity<ObjetivosDTOResponse> updateObjetivos(@RequestBody ObjetivosDTORequest objetivosDTORequest,
                                                                @PathVariable Long prjCod,
                                                                @PathVariable Long objCod){
        ObjetivosDTOResponse objetivosDTOResponse = planejamentoEstrategicoService.saveObjetivos(objetivosDTORequest,
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
    public ResponseEntity<List<TarefasDTOResponse>> findTarefasByObjetivos(@PathVariable Long objCod){
        List<TarefasDTOResponse> tarefasDTOResponse = planejamentoEstrategicoService.findTarefasByObjetivos(objCod);

        return ResponseEntity.ok(tarefasDTOResponse);
    }

    @GetMapping("{pesCod}/projetos/{prjCod}/objetivos/{objCod}/tarefas/{trfCod}")
    public ResponseEntity<TarefasDTOResponse> findByTarefaId(@PathVariable Long trfCod){
        TarefasDTOResponse tarefasDTOResponse = planejamentoEstrategicoService.findByTarefaId(trfCod);

        return ResponseEntity.ok(tarefasDTOResponse);
    }

    @PostMapping("{pesCod}/projetos/{prjCod}/objetivos/{objCod}/tarefas")
    public ResponseEntity<TarefasDTOResponse> insertTarefas(@RequestBody TarefasDTORequest tarefasDTORequest,
                                                            @PathVariable Long objCod){
        TarefasDTOResponse tarefasDTOResponse = planejamentoEstrategicoService.saveTarefas(tarefasDTORequest,
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
    public ResponseEntity<TarefasDTOResponse> updateTarefas(@RequestBody TarefasDTORequest tarefasDTORequest,
                                                            @PathVariable Long objCod,
                                                            @PathVariable Long trfCod){

        TarefasDTOResponse tarefasDTOResponse = planejamentoEstrategicoService.saveTarefas(tarefasDTORequest,
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
    public ResponseEntity<List<EvidenciasDTOResponse>> findEvidenciasByTarefas(@PathVariable Long trfCod){
        return ResponseEntity.ok(planejamentoEstrategicoService.findEvidenciasByTarefas(trfCod));
    }

    @PostMapping("{pesCod}/projetos/{prjCod}/objetivos/{objCod}/tarefas/{trfCod}/evidencias")
    public ResponseEntity<EvidenciasDTOResponse> insertEvidencias(@RequestBody EvidenciasDTORequest evidenciasDTORequest){
        EvidenciasDTOResponse evidenciasDTOResponse = planejamentoEstrategicoService.saveEvidencia(evidenciasDTORequest, null);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{evdCod}")
                .buildAndExpand(evidenciasDTOResponse.evdCod())
                .toUri();

        return ResponseEntity.created(location).body(evidenciasDTOResponse);
    }

    @PostMapping("{pesCod}/projetos/{prjCod}/objetivos/{objCod}/tarefas/{trfCod}/evidencias/{evdCod}")
    public ResponseEntity<EvidenciasDTOResponse> updateEvidencias(@RequestBody EvidenciasDTORequest evidenciasDTORequest,
                                                                  @PathVariable Long evdCod){
        return ResponseEntity.ok(planejamentoEstrategicoService.saveEvidencia(evidenciasDTORequest, evdCod));
    }

    @DeleteMapping("{pesCod}/projetos/{prjCod}/objetivos/{objCod}/tarefas/{trfCod}/evidencias/{evdCod}")
    public ResponseEntity<Void> deleteEvidencias(@PathVariable Long evdCod){
        planejamentoEstrategicoService.deleteEvidencia(evdCod);

        return ResponseEntity.noContent().build();
    }
}


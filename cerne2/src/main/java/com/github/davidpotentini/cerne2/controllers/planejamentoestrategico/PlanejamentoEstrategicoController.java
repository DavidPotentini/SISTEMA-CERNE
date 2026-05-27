package com.github.davidpotentini.cerne2.controllers.planejamentoestrategico;

import com.github.davidpotentini.cerne2.configuration.security.annotations.AcessoIncubada;
import com.github.davidpotentini.cerne2.dto.arquivo.ArquivoDTO;
import com.github.davidpotentini.cerne2.dto.planejamentoestrategico.*;
import com.github.davidpotentini.cerne2.models.arquivo.ids.ArquivosEvidenciasId;
import com.github.davidpotentini.cerne2.service.arquivos.ArquivoService;
import com.github.davidpotentini.cerne2.service.planejamentoestrategico.PlanejamentoEstrategicoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
public class PlanejamentoEstrategicoController {

    private final PlanejamentoEstrategicoService planejamentoEstrategicoService;
    private final ArquivoService arquivoService;

    public PlanejamentoEstrategicoController(PlanejamentoEstrategicoService planejamentoEstrategicoService,
                                             ArquivoService arquivoService) {
        this.planejamentoEstrategicoService = planejamentoEstrategicoService;
        this.arquivoService = arquivoService;
    }

    /*
    *
    * Planos Anuais Integrados
    *
    */

    @GetMapping({"/planejamento", "/incubadas/{incCod}/planejamento"})
    @AcessoIncubada
    public ResponseEntity<List<PlanejamentoEstrategicoDTO>> findList(@PathVariable(required = false) Long incCod){
        List<PlanejamentoEstrategicoDTO> planejamentoEstrategicoDTO = planejamentoEstrategicoService.findList(incCod);

        return ResponseEntity.ok(planejamentoEstrategicoDTO);
    }

    @GetMapping({"/planejamento/{pesCod}", "/incubadas/{incCod}/planejamento/{pesCod}"})
    @AcessoIncubada
    public ResponseEntity<PlanejamentoEstrategicoDTO> findById(@PathVariable Long pesCod,
                                                               @PathVariable(required = false) Long incCod){
        PlanejamentoEstrategicoDTO planejamentoEstrategicoDTOResponse = planejamentoEstrategicoService.findById(pesCod);

        return ResponseEntity.ok(planejamentoEstrategicoDTOResponse);
    }

    @PostMapping({"/planejamento", "/incubadas/{incCod}/planejamento"})
    @AcessoIncubada
    public ResponseEntity<PlanejamentoEstrategicoDTO> insert(@RequestBody PlanejamentoEstrategicoDTO planejamentoEstrategicoDTORequest,
                                                             @PathVariable(required = false) Long incCod){
        PlanejamentoEstrategicoDTO planosDTO = planejamentoEstrategicoService.save(planejamentoEstrategicoDTORequest, null, incCod);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{pesCod}")
                .buildAndExpand(planosDTO.pesCod())
                .toUri();

        return ResponseEntity.created(location).body(planosDTO);
    }

    @PutMapping({"/planejamento/{pesCod}", "/incubadas/{incCod}/planejamento/{pesCod}"})
    @AcessoIncubada
    public ResponseEntity<PlanejamentoEstrategicoDTO> update(@RequestBody PlanejamentoEstrategicoDTO planejamentoEstrategicoDTORequest,
                                                             @PathVariable Long pesCod,
                                                             @PathVariable(required = false) Long incCod) {

        PlanejamentoEstrategicoDTO planosDTO = planejamentoEstrategicoService.save(planejamentoEstrategicoDTORequest, pesCod, incCod);

        return ResponseEntity.ok(planosDTO);
    }

    @DeleteMapping({"/planejamento/{pesCod}", "/incubadas/{incCod}/planejamento/{pesCod}"})
    @AcessoIncubada
    public ResponseEntity<Void> delete(@PathVariable Long pesCod,
                                       @PathVariable(required = false) Long incCod) {
        planejamentoEstrategicoService.delete(pesCod);

        return ResponseEntity.noContent().build();
    }

    /*
     *
     * Projetos
     *
     */

    @GetMapping({"/planejamento/{pesCod}/projetos", "/incubadas/{incCod}/planejamento/{pesCod}/projetos"})
    @AcessoIncubada
    public ResponseEntity<List<ProjetosDTO>> findProjetoByPlano (@PathVariable Long pesCod,
                                                                 @PathVariable(required = false) Long incCod) {
        List<ProjetosDTO> projetosDTOResponseList = planejamentoEstrategicoService.findProjetoByPlano(pesCod);

        return ResponseEntity.ok(projetosDTOResponseList);
    }

    @GetMapping({"/planejamento/{pesCod}/projetos/{prjCod}", "/incubadas/{incCod}/planejamento/{pesCod}/projetos/{prjCod}"})
    @AcessoIncubada
    public ResponseEntity<ProjetosDTO> findByProjetoId(@PathVariable Long prjCod,
                                                       @PathVariable(required = false) Long incCod){
        ProjetosDTO projetosDTOResponse = planejamentoEstrategicoService.findByProjetoId(prjCod);

        return ResponseEntity.ok(projetosDTOResponse);
    }

    @PostMapping({"/planejamento/{pesCod}/projetos", "/incubadas/{incCod}/planejamento/{pesCod}/projetos"})
    @AcessoIncubada
    public ResponseEntity<ProjetosDTO> insertProjetos(@RequestBody ProjetosDTO projetosDTORequest,
                                                      @PathVariable Long pesCod,
                                                      @PathVariable(required = false) Long incCod){
        ProjetosDTO projetosDTOResponse = planejamentoEstrategicoService.saveProjetos(projetosDTORequest, pesCod, null);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/prjCod")
                .buildAndExpand(projetosDTOResponse.prjCod())
                .toUri();

        return ResponseEntity.created(location).body(projetosDTOResponse);
    }

    @PutMapping({"/planejamento/{pesCod}/projetos/{prjCod}", "/incubadas/{incCod}/planejamento/{pesCod}/projetos/{prjCod}"})
    @AcessoIncubada
    public ResponseEntity<ProjetosDTO> updateProjetos(@RequestBody ProjetosDTO projetosDTORequest,
                                                      @PathVariable Long pesCod,
                                                      @PathVariable Long prjCod,
                                                      @PathVariable(required = false) Long incCod){
        ProjetosDTO projetosDTOResponse = planejamentoEstrategicoService.saveProjetos(projetosDTORequest, pesCod, prjCod);

        return ResponseEntity.ok(projetosDTOResponse);
    }

    @DeleteMapping({"/planejamento/{pesCod}/projetos/{prjCod}", "/incubadas/{incCod}/planejamento/{pesCod}/projetos/{prjCod}"})
    @AcessoIncubada
    public ResponseEntity<Void> deleteProjetos (@PathVariable Long prjCod,
                                                @PathVariable(required = false) Long incCod) {
        planejamentoEstrategicoService.deleteProjetos(prjCod);

        return ResponseEntity.noContent().build();
    }

    /*
     *
     * Objetivos
     *
     */

    @GetMapping({"/planejamento/{pesCod}/projetos/{prjCod}/objetivos", "/incubadas/{incCod}/planejamento/{pesCod}/projetos/{prjCod}/objetivos"})
    @AcessoIncubada
    public ResponseEntity<List<ObjetivosDTO>> findObjetivosByProjetos(@PathVariable Long prjCod,
                                                                      @PathVariable(required = false) Long incCod){
        List<ObjetivosDTO> objetivosDTOResponse = planejamentoEstrategicoService.findObjetivosByProjetos(prjCod);

        return ResponseEntity.ok(objetivosDTOResponse);
    }

    @GetMapping({"/planejamento/{pesCod}/projetos/{prjCod}/objetivos/{objCod}", "/incubadas/{incCod}/planejamento/{pesCod}/projetos/{prjCod}/objetivos/{objCod}"})
    @AcessoIncubada
    public ResponseEntity<ObjetivosDTO> findByObjetivosId(@PathVariable Long objCod,
                                                          @PathVariable(required = false) Long incCod){
        ObjetivosDTO objetivosDTOResponse = planejamentoEstrategicoService.findByObjetivosId(objCod);

        return ResponseEntity.ok(objetivosDTOResponse);
    }

    @PostMapping({"/planejamento/{pesCod}/projetos/{prjCod}/objetivos", "/incubadas/{incCod}/planejamento/{pesCod}/projetos/{prjCod}/objetivos"})
    @AcessoIncubada
    public ResponseEntity<ObjetivosDTO> insertObjetivos(@RequestBody ObjetivosDTO ObjetivosDTO,
                                                        @PathVariable Long prjCod,
                                                        @PathVariable(required = false) Long incCod){
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

    @PutMapping({"/planejamento/{pesCod}/projetos/{prjCod}/objetivos/{objCod}", "/incubadas/{incCod}/planejamento/{pesCod}/projetos/{prjCod}/objetivos/{objCod}"})
    @AcessoIncubada
    public ResponseEntity<ObjetivosDTO> updateObjetivos(@RequestBody ObjetivosDTO objetivosDTORequest,
                                                        @PathVariable Long prjCod,
                                                        @PathVariable Long objCod,
                                                        @PathVariable(required = false) Long incCod){
        ObjetivosDTO objetivosDTOResponse = planejamentoEstrategicoService.saveObjetivos(objetivosDTORequest,
                                                                                                 prjCod,
                                                                                                 objCod);

        return ResponseEntity.ok(objetivosDTOResponse);
    }

    @DeleteMapping({"/planejamento/{pesCod}/projetos/{prjCod}/objetivos/{objCod}", "/incubadas/{incCod}/planejamento/{pesCod}/projetos/{prjCod}/objetivos/{objCod}"})
    @AcessoIncubada
    public ResponseEntity<Void> deleteObjetivos(@PathVariable Long objCod,
                                                @PathVariable(required = false) Long incCod){
        planejamentoEstrategicoService.deleteObjetivos(objCod);

        return ResponseEntity.noContent().build();
    }

    /*
    *
    * Tarefas
    *
    */

    @GetMapping({"/planejamento/{pesCod}/projetos/{prjCod}/objetivos/{objCod}/tarefas", "/incubadas/{incCod}/planejamento/{pesCod}/projetos/{prjCod}/objetivos/{objCod}/tarefas"})
    @AcessoIncubada
    public ResponseEntity<List<TarefasDTO>> findTarefasByObjetivos(@PathVariable Long objCod,
                                                                   @PathVariable(required = false) Long incCod){
        List<TarefasDTO> tarefasDTOResponse = planejamentoEstrategicoService.findTarefasByObjetivos(objCod);

        return ResponseEntity.ok(tarefasDTOResponse);
    }

    @GetMapping({"/planejamento/{pesCod}/projetos/{prjCod}/objetivos/{objCod}/tarefas/{trfCod}", "/incubadas/{incCod}/planejamento/{pesCod}/projetos/{prjCod}/objetivos/{objCod}/tarefas/{trfCod}"})
    @AcessoIncubada
    public ResponseEntity<TarefasDTO> findByTarefaId(@PathVariable Long trfCod,
                                                     @PathVariable(required = false) Long incCod){
        TarefasDTO tarefasDTOResponse = planejamentoEstrategicoService.findByTarefaId(trfCod);

        return ResponseEntity.ok(tarefasDTOResponse);
    }

    @PostMapping({"/planejamento/{pesCod}/projetos/{prjCod}/objetivos/{objCod}/tarefas", "/incubadas/{incCod}/planejamento/{pesCod}/projetos/{prjCod}/objetivos/{objCod}/tarefas"})
    @AcessoIncubada
    public ResponseEntity<TarefasDTO> insertTarefas(@RequestBody TarefasDTO tarefasDTORequest,
                                                    @PathVariable Long objCod,
                                                    @PathVariable(required = false) Long incCod){
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

    @PutMapping({"/planejamento/{pesCod}/projetos/{prjCod}/objetivos/{objCod}/tarefas/{trfCod}", "/incubadas/{incCod}/planejamento/{pesCod}/projetos/{prjCod}/objetivos/{objCod}/tarefas/{trfCod}"})
    @AcessoIncubada
    public ResponseEntity<TarefasDTO> updateTarefas(@RequestBody TarefasDTO tarefasDTORequest,
                                                    @PathVariable Long objCod,
                                                    @PathVariable Long trfCod,
                                                    @PathVariable(required = false) Long incCod){

        TarefasDTO tarefasDTOResponse = planejamentoEstrategicoService.saveTarefas(tarefasDTORequest,
                                                                                           objCod,
                                                                                           trfCod);

        return ResponseEntity.ok(tarefasDTOResponse);
    }

    @DeleteMapping({"/planejamento/{pesCod}/projetos/{prjCod}/objetivos/{objCod}/tarefas/{trfCod}", "/incubadas/{incCod}/planejamento/{pesCod}/projetos/{prjCod}/objetivos/{objCod}/tarefas/{trfCod}"})
    @AcessoIncubada
    public ResponseEntity<Void> deleteTarefas(@PathVariable Long trfCod,
                                              @PathVariable(required = false) Long incCod){
        planejamentoEstrategicoService.deleteTarefas(trfCod);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/incubadas/{incCod}/planejamento/{pesCod}/projetos/{prjCod}/objetivos/{objCod}/tarefas/{trfCod}/avaliacao")
    @AcessoIncubada
    public ResponseEntity<TarefasDTO> avaliarTarefa(@PathVariable Long incCod,
                                                    @PathVariable Long trfCod,
                                                    @RequestBody AvaliacaoTarefaDTO avaliacaoTarefaDTO){
        return ResponseEntity.ok(planejamentoEstrategicoService.avaliarTarefa(trfCod, avaliacaoTarefaDTO));
    }

    /*
    *
    * Evidencias
    *
    */

    @GetMapping({"/planejamento/{pesCod}/projetos/{prjCod}/objetivos/{objCod}/tarefas/{trfCod}/evidencias", "/incubadas/{incCod}/planejamento/{pesCod}/projetos/{prjCod}/objetivos/{objCod}/tarefas/{trfCod}/evidencias"})
    @AcessoIncubada
    public ResponseEntity<List<EvidenciasDTO>> findEvidenciasByTarefas(@PathVariable Long trfCod,
                                                                       @PathVariable(required = false) Long incCod){
        return ResponseEntity.ok(planejamentoEstrategicoService.findEvidenciasByTarefas(trfCod));
    }

    @PostMapping({"/planejamento/{pesCod}/projetos/{prjCod}/objetivos/{objCod}/tarefas/{trfCod}/evidencias", "/incubadas/{incCod}/planejamento/{pesCod}/projetos/{prjCod}/objetivos/{objCod}/tarefas/{trfCod}/evidencias"})
    @AcessoIncubada
    public ResponseEntity<EvidenciasDTO> insertEvidencias(@RequestBody EvidenciasDTO evidenciasDTORequest,
                                                          @PathVariable(required = false) Long incCod){
        EvidenciasDTO evidenciasDTOResponse = planejamentoEstrategicoService.saveEvidencia(evidenciasDTORequest, null);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{evdCod}")
                .buildAndExpand(evidenciasDTOResponse.evdCod())
                .toUri();

        return ResponseEntity.created(location).body(evidenciasDTOResponse);
    }

    @PostMapping({"/planejamento/{pesCod}/projetos/{prjCod}/objetivos/{objCod}/tarefas/{trfCod}/evidencias/{evdCod}", "/incubadas/{incCod}/planejamento/{pesCod}/projetos/{prjCod}/objetivos/{objCod}/tarefas/{trfCod}/evidencias/{evdCod}"})
    @AcessoIncubada
    public ResponseEntity<EvidenciasDTO> updateEvidencias(@RequestBody EvidenciasDTO evidenciasDTORequest,
                                                          @PathVariable Long evdCod,
                                                          @PathVariable(required = false) Long incCod){
        return ResponseEntity.ok(planejamentoEstrategicoService.saveEvidencia(evidenciasDTORequest, evdCod));
    }

    @DeleteMapping({"/planejamento/{pesCod}/projetos/{prjCod}/objetivos/{objCod}/tarefas/{trfCod}/evidencias/{evdCod}", "/incubadas/{incCod}/planejamento/{pesCod}/projetos/{prjCod}/objetivos/{objCod}/tarefas/{trfCod}/evidencias/{evdCod}"})
    @AcessoIncubada
    public ResponseEntity<Void> deleteEvidencias(@PathVariable Long evdCod,
                                                 @PathVariable(required = false) Long incCod){
        planejamentoEstrategicoService.deleteEvidencia(evdCod);

        return ResponseEntity.noContent().build();
    }

    @GetMapping({"/planejamento/{pesCod}/projetos/{prjCod}/objetivos/{objCod}/tarefas/{trfCod}/evidencias/{evdCod}/arquivos", "/incubadas/{incCod}/planejamento/{pesCod}/projetos/{prjCod}/objetivos/{objCod}/tarefas/{trfCod}/evidencias/{evdCod}/arquivos"})
    @AcessoIncubada
    public ResponseEntity<List<ArquivoDTO>> findArquivosEvidencias(@PathVariable Long evdCod,
                                                                   @PathVariable(required = false) Long incCod){
        return ResponseEntity.ok(planejamentoEstrategicoService.findArquivosEvidencias(evdCod));
    }

    @PostMapping({"/planejamento/{pesCod}/projetos/{prjCod}/objetivos/{objCod}/tarefas/{trfCod}/evidencias/{evdCod}/arquivos", "/incubadas/{incCod}/planejamento/{pesCod}/projetos/{prjCod}/objetivos/{objCod}/tarefas/{trfCod}/evidencias/{evdCod}/arquivos"})
    @AcessoIncubada
    public ResponseEntity<ArquivoDTO> uploadArquivoEvidencias (@PathVariable Long evdCod,
                                                               @PathVariable(required = false) Long incCod,
                                                               MultipartFile multipartFile){

        ArquivoDTO arquivoDTO = planejamentoEstrategicoService.uploadArquivosEvidencias(multipartFile, evdCod);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{arqCod}")
                .buildAndExpand(arquivoDTO.arqCod())
                .toUri();

        return ResponseEntity.created(location).body(arquivoDTO);
    }

    @GetMapping({"/planejamento/{pesCod}/projetos/{prjCod}/objetivos/{objCod}/tarefas/{trfCod}/evidencias/{evdCod}/arquivos/{arqCod}", "/incubadas/{incCod}/planejamento/{pesCod}/projetos/{prjCod}/objetivos/{objCod}/tarefas/{trfCod}/evidencias/{evdCod}/arquivos/{arqCod}"})
    @AcessoIncubada
    public ResponseEntity<String> downloadArquivoEvidencias(@PathVariable Long arqCod,
                                                            @PathVariable(required = false) Long incCod){
        return ResponseEntity.ok(arquivoService.gerarUrlDownload(arqCod));
    }

    @DeleteMapping({"/planejamento/{pesCod}/projetos/{prjCod}/objetivos/{objCod}/tarefas/{trfCod}/evidencias/{evdCod}/arquivos/{arqCod}", "/incubadas/{incCod}/planejamento/{pesCod}/projetos/{prjCod}/objetivos/{objCod}/tarefas/{trfCod}/evidencias/{evdCod}/arquivos/{arqCod}"})
    @AcessoIncubada
    public ResponseEntity<Void> deleteArquivoEvidencias(@PathVariable Long arqCod,
                                                        @PathVariable(required = false) Long incCod){
        arquivoService.deletar(arqCod);

        return ResponseEntity.noContent().build();
    }
}

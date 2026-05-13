package com.github.davidpotentini.cerne2.controllers.CanvasController;

import com.github.davidpotentini.cerne2.dto.canvas.*;
import com.github.davidpotentini.cerne2.service.canvas.CanvasService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Controller
@RequestMapping("/{incCod}/ambienteCanvas")
public class CanvasController {

    private final CanvasService canvasService;

    public CanvasController(CanvasService canvasService) {
        this.canvasService = canvasService;
    }

    /*
    *
    *  Ambiente Canvas
    *
    */

    @GetMapping
    public ResponseEntity<List<AmbienteCanvasDTO>> findAmbienteCanvasByIncCod(@PathVariable Long incCod){
        return ResponseEntity.ok(canvasService.findAmbienteCanvasByIncCod(incCod));
    }

    @GetMapping("/{ambcCod}")
    public ResponseEntity<AmbienteCanvasDTO> findAmbienteCanvasById(@PathVariable Long ambcCod){
        return ResponseEntity.ok(canvasService.findAmbienteCanvasById(ambcCod));
    }

    @PostMapping
    public ResponseEntity<AmbienteCanvasDTO> insertAmbienteCanvas(@RequestBody AmbienteCanvasDTO ambienteCanvasDTO){
        AmbienteCanvasDTO ambienteCanvasDTO2 = canvasService.saveAmbienteCanvas(ambienteCanvasDTO, null);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("{/ambcCod}")
                .buildAndExpand(ambienteCanvasDTO2.ambcCod())
                .toUri();

        return ResponseEntity.created(location).body(ambienteCanvasDTO2);
    }

    @PutMapping("/{ambcCod}")
    public ResponseEntity<AmbienteCanvasDTO> updateAmbienteCanvas(@RequestBody AmbienteCanvasDTO ambienteCanvasDTO, @PathVariable Long ambcCod){
        return ResponseEntity.ok(canvasService.saveAmbienteCanvas(ambienteCanvasDTO, ambcCod));
    }

    @DeleteMapping("/{ambcCod}")
    public ResponseEntity<Void> deleteAmbienteCanvas(Long ambcCod){
        canvasService.deleteAmbienteCanvas(ambcCod);

        return ResponseEntity.noContent().build();
    }

    /*
    *
    * Business Model Canvas
    *
    */

    @GetMapping("/{ambcCod}/businessModelCanvas")
    public ResponseEntity<BusinessModelCanvasDTO> findByBusinessModelCanvasById(Long ambcCod){
        return ResponseEntity.ok(canvasService.findByBusinessModelCanvasById(ambcCod));
    }

    @PostMapping("/{ambcCod}/businessModelCanvas")
    public ResponseEntity<BusinessModelCanvasDTO> insertBusinessModelCanvas(@RequestBody BusinessModelCanvasDTO businessModelCanvasDTO,
                                                                            @PathVariable Long ambcCod){
        BusinessModelCanvasDTO businessModelCanvasDTO2 = canvasService.saveBusinessModelCanvas(businessModelCanvasDTO, ambcCod);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/businessModelCanvas")
                .build()
                .toUri();

        return ResponseEntity.created(location).body(businessModelCanvasDTO2);
    }

    @PutMapping("/{ambcCod}/businessModelCanvas")
    public ResponseEntity<BusinessModelCanvasDTO> updateBusinessModelCanvas(@RequestBody BusinessModelCanvasDTO businessModelCanvasDTO,
                                                                            @PathVariable Long ambcCod){

        return ResponseEntity.ok(canvasService.saveBusinessModelCanvas(businessModelCanvasDTO, ambcCod));
    }

    @DeleteMapping("/{ambcCod}/businessModelCanvas")
    public ResponseEntity<Void> deleteBusinessModelCanvas(@PathVariable Long ambcCod){
        canvasService.deleteBusinessModelCanvas(ambcCod);

        return ResponseEntity.noContent().build();
    }

    /*
    *
    * Channel Implementation Canvas
    *
    */

    @GetMapping("/{ambcCod}/channelImplementationCanvas")
    public ResponseEntity<List<ChannelImplementationCanvasDTO>> findChannelImplementationCanvasByAmbcCod(@PathVariable Long ambcCod){
        return ResponseEntity.ok(canvasService.findChannelImplementationCanvasByAmbcCod(ambcCod));
    }

    @GetMapping("/{ambcCod}/channelImplementationCanvas/{segCod}")
    public ResponseEntity<ChannelImplementationCanvasDTO> findChannelImplementationCanvasById(@PathVariable Long segCod){
        return ResponseEntity.ok(canvasService.findChannelImplementationCanvasById(segCod));
    }

    @PostMapping("/{ambcCod}/channelImplementationCanvas")
    public ResponseEntity<ChannelImplementationCanvasDTO> insertChannelImplementationCanvas(@RequestBody ChannelImplementationCanvasDTO channelImplementationCanvasDTO,
                                                                                            @PathVariable Long ambcCod){
        ChannelImplementationCanvasDTO channelImplementationCanvasDTO2 = canvasService.saveChannelImplementationCanvas(channelImplementationCanvasDTO,null, ambcCod);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{segCod}")
                .buildAndExpand(channelImplementationCanvasDTO2.segCod())
                .toUri();

        return ResponseEntity.created(location).body(channelImplementationCanvasDTO2);
    }

    @PutMapping("/{ambcCod}/channelImplementationCanvas/{segCod}")
    public ResponseEntity<ChannelImplementationCanvasDTO> updateChannelImplementationCanvas(@RequestBody ChannelImplementationCanvasDTO channelImplementationCanvasDTO,
                                                                                            @PathVariable Long ambcCod,
                                                                                            @PathVariable Long segCod){

        return ResponseEntity.ok(canvasService.saveChannelImplementationCanvas(channelImplementationCanvasDTO,segCod, ambcCod));
    }

    @DeleteMapping("/{ambcCod}/channelImplementationCanvas/{segCod}")
    public ResponseEntity<Void> deleteChannelImplementationCanvas(@PathVariable Long segCod){
        canvasService.deleteChannelImplementationCanvas(segCod);

        return ResponseEntity.noContent().build();
    }

    /*
    *
    * Customer Personas Canvas
    *
    */

    @GetMapping("/{ambcCod}/customerPersonasCanvas")
    public ResponseEntity<List<CustomerPersonasCanvasDTO>> findCustomerPersonasByAmbcCod(Long ambcCod){
        return ResponseEntity.ok(canvasService.findCustomerPersonasByAmbcCod(ambcCod));
    }

    @GetMapping("/{ambcCod}/customerPersonasCanvas/{personaCod}")
    public ResponseEntity<CustomerPersonasCanvasDTO> findCustomerPersonasCanvasById(Long personaCod){
        return ResponseEntity.ok(canvasService.findCustomerPersonasCanvasById(personaCod));
    }

    @PostMapping("/{ambcCod}/customerPersonasCanvas")
    public ResponseEntity<CustomerPersonasCanvasDTO> insertCustomerPersonasCanvas(@RequestBody CustomerPersonasCanvasDTO customerPersonasCanvasDTO,
                                                                                  @PathVariable Long ambcCod){
        CustomerPersonasCanvasDTO customerPersonasCanvasDTO2 = canvasService.saveCustomerPersonasCanvas(customerPersonasCanvasDTO, null, ambcCod);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{personaCod}")
                .buildAndExpand(customerPersonasCanvasDTO2.personaCod())
                .toUri();

        return ResponseEntity.created(location).body(customerPersonasCanvasDTO2);
    }

    @PutMapping("/{ambcCod}/customerPersonaCanvas/{personaCod}")
    public ResponseEntity<CustomerPersonasCanvasDTO> updateCustomerPersonasCanvas(@RequestBody CustomerPersonasCanvasDTO customerPersonasCanvasDTO,
                                                                                  @PathVariable Long ambcCod,
                                                                                  @PathVariable Long personaCod){
        return ResponseEntity.ok(canvasService.saveCustomerPersonasCanvas(customerPersonasCanvasDTO, personaCod, ambcCod));

    }

    @DeleteMapping("/{ambcCod}/customerPersonaCanvas/{personaCod}")
    public ResponseEntity<Void> deleteCustomerPersonasCanvas(@PathVariable Long personaCod){

    canvasService.deleteCustomerPersonasCanvas(personaCod);

    return ResponseEntity.noContent().build();
    }


    /*
    *
    * Lean Canvas
    *
    */

    @GetMapping("/{ambcCod}/leanCanvas")
    public ResponseEntity<LeanCanvasDTO> findLeanCanvasById(@PathVariable Long ambcCod){
        return ResponseEntity.ok(canvasService.findLeanCanvasById(ambcCod));
    }

    @PostMapping("/{ambcCod}/leanCanvas")
    public ResponseEntity<LeanCanvasDTO> insertLeanCanvas(@PathVariable LeanCanvasDTO leanCanvasDTO,
                                                          @PathVariable Long ambcCod){
        LeanCanvasDTO leanCanvasDTO2 = canvasService.saveLeanCanvas(leanCanvasDTO, ambcCod);


        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/leanCanvas")
                .build()
                .toUri();

        return ResponseEntity.created(location).body(leanCanvasDTO2);
    }

    @PutMapping("/{ambcCod}/leanCanvas")
    public ResponseEntity<LeanCanvasDTO> updateLeanCanvas(@PathVariable LeanCanvasDTO leanCanvasDTO,
                                                          @PathVariable Long ambcCod){

        return ResponseEntity.ok(canvasService.saveLeanCanvas(leanCanvasDTO, ambcCod));
    }

    @DeleteMapping("/{ambcCod}/leanCanvas")
    public ResponseEntity<Void> deleteLeanCanvas(@PathVariable Long ambcCod){
        canvasService.deleteLeanCanvas(ambcCod);

        return ResponseEntity.noContent().build();
    }

    /*
    *
    * Value Proposition Canvas
    *
    */

    @GetMapping("/{ambcCod}/valuePropositionCanvas")
    public ResponseEntity<ValuePropositionCanvasDTO> findValuePropositionCanvasById(@PathVariable Long ambcCod){
        return ResponseEntity.ok(canvasService.findValuePropositionCanvasById(ambcCod));
    }

    @PostMapping("/{ambcCod}/valuePropositionCanvas")
    public ResponseEntity<ValuePropositionCanvasDTO> insertValuePropositionCanvas(@PathVariable ValuePropositionCanvasDTO valuePropositionCanvasDTO,
                                                          @PathVariable Long ambcCod){
        ValuePropositionCanvasDTO valuePropositionCanvasDTO2 = canvasService.saveValuePropositionCanvas(valuePropositionCanvasDTO, ambcCod);


        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/valuePropositionCanvas")
                .build()
                .toUri();

        return ResponseEntity.created(location).body(valuePropositionCanvasDTO2);
    }

    @PutMapping("/{ambcCod}/valuePropositionCanvas")
    public ResponseEntity<ValuePropositionCanvasDTO> updateValuePropositionCanvas(@PathVariable ValuePropositionCanvasDTO valuePropositionCanvasDTO,
                                                          @PathVariable Long ambcCod){

        return ResponseEntity.ok(canvasService.saveValuePropositionCanvas(valuePropositionCanvasDTO, ambcCod));
    }

    @DeleteMapping("/{ambcCod}/valuePropositionCanvas")
    public ResponseEntity<Void> deleteValuePropositionCanvas(@PathVariable Long ambcCod) {
        canvasService.deleteValuePropositionCanvas(ambcCod);

        return ResponseEntity.noContent().build();
    }
}

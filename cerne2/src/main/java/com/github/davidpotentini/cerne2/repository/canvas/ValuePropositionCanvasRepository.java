package com.github.davidpotentini.cerne2.repository.canvas;

import com.github.davidpotentini.cerne2.models.canvas.ValuePropostionCanvasModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ValuePropositionCanvasRepository extends JpaRepository<ValuePropostionCanvasModel, Long> {

    Optional<ValuePropostionCanvasModel> findByAmbienteCanvasModel_AmbcCod(Long ambcCod);

    void deleteByAmbienteCanvasModel_AmbcCod(Long ambcCod);
}

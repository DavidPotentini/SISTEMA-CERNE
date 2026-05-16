package com.github.davidpotentini.cerne2.repository.canvas;

import com.github.davidpotentini.cerne2.models.canvas.LeanCanvasModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LeanCanvasRepository extends JpaRepository<LeanCanvasModel, Long> {

    Optional<LeanCanvasModel> findByAmbienteCanvasModel_AmbcCod(Long ambcCod);

    void deleteByAmbienteCanvasModel_AmbcCod(Long ambcCod);

}

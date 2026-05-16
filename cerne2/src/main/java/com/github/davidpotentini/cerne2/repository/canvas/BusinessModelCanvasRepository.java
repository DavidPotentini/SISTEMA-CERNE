package com.github.davidpotentini.cerne2.repository.canvas;

import com.github.davidpotentini.cerne2.models.canvas.BusinessModelCanvasModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BusinessModelCanvasRepository extends JpaRepository<BusinessModelCanvasModel, Long> {

    Optional<BusinessModelCanvasModel> findByAmbienteCanvasModel_AmbcCod(Long ambcCod);

    void deleteByAmbienteCanvasModel_AmbcCod(Long ambcCod);
}

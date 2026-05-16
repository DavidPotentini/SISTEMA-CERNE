package com.github.davidpotentini.cerne2.repository.canvas;

import com.github.davidpotentini.cerne2.models.canvas.AmbienteCanvasModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AmbienteCanvasRepository extends JpaRepository<AmbienteCanvasModel, Long> {

    @Query(value = "SELECT * FROM AMBIENTE_CANVAS WHERE INC_COD = :INC_COD", nativeQuery = true)
    List<AmbienteCanvasModel> findAmbienteCanvasByIncCod(@Param("INC_COD") Long incCod);
}

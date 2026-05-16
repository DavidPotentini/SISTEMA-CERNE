package com.github.davidpotentini.cerne2.repository.canvas;

import com.github.davidpotentini.cerne2.models.canvas.CustomerPersonasCanvasModel;
import com.github.davidpotentini.cerne2.models.canvas.ids.CustomerPersonasCanvasId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomerPersonasCanvasRepository extends JpaRepository<CustomerPersonasCanvasModel, Long> {


    List<CustomerPersonasCanvasModel> findByAmbienteCanvasModel_AmbcCod(Long ambcCod);

    void deleteByAmbienteCanvasModel_AmbcCod(Long ambcCod);

}

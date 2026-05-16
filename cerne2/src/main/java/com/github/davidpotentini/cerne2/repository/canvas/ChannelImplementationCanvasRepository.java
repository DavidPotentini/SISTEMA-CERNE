package com.github.davidpotentini.cerne2.repository.canvas;

import com.github.davidpotentini.cerne2.models.canvas.ChannelImplementationCanvasModel;
import com.github.davidpotentini.cerne2.models.canvas.ids.ChannelImplementationCanvasId;
import org.hibernate.annotations.processing.SQL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChannelImplementationCanvasRepository extends JpaRepository<ChannelImplementationCanvasModel, Long> {


    List<ChannelImplementationCanvasModel> findByAmbienteCanvasModel_AmbcCod(Long ambcCod);

    void deleteByAmbienteCanvasModel_AmbcCod(Long ambcCod);

}

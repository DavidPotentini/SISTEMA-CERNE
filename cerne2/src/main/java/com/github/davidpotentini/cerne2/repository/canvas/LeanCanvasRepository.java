package com.github.davidpotentini.cerne2.repository.canvas;

import com.github.davidpotentini.cerne2.models.canvas.LeanCanvasModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeanCanvasRepository extends JpaRepository<LeanCanvasModel, Long> {
}

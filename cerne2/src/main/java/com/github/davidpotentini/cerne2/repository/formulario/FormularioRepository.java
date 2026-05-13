package com.github.davidpotentini.cerne2.repository.formulario;

import com.github.davidpotentini.cerne2.models.formularios.FormularioModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FormularioRepository extends JpaRepository<FormularioModel, Long> {
}

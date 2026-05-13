package com.github.davidpotentini.cerne2.repository.formulario;

import com.github.davidpotentini.cerne2.models.formularios.FormularioRespostasModel;
import com.github.davidpotentini.cerne2.models.formularios.ids.FormularioRespostasId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FormularioRespostasRepository extends JpaRepository<FormularioRespostasModel, FormularioRespostasId> {
}

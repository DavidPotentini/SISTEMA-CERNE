package com.github.davidpotentini.cerne2.repository.arquivo;

import com.github.davidpotentini.cerne2.models.arquivo.ArquivosModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArquivoRepository extends JpaRepository<ArquivosModel, Long> {
}

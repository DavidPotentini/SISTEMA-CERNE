package com.github.davidpotentini.repository.arquivo;

import com.github.davidpotentini.model.arquivo.ArquivoModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArquivoRepository extends JpaRepository<ArquivoModel, Long> {
}

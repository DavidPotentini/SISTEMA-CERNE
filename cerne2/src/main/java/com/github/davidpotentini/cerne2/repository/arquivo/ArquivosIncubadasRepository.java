package com.github.davidpotentini.cerne2.repository.arquivo;

import com.github.davidpotentini.cerne2.models.arquivo.ArquivosIncubadasModel;
import com.github.davidpotentini.cerne2.models.arquivo.ids.ArquivosIcubadasId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArquivosIncubadasRepository extends JpaRepository<ArquivosIncubadasModel, ArquivosIcubadasId> {

    List<ArquivosIncubadasModel> findByIncubadasModel_IncCod(Long incCod);
}

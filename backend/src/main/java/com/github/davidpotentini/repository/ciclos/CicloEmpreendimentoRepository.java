package com.github.davidpotentini.repository.ciclos;

import com.github.davidpotentini.model.ciclos.CicloEmpreendimentoId;
import com.github.davidpotentini.model.ciclos.CicloEmpreendimentoModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CicloEmpreendimentoRepository
        extends JpaRepository<CicloEmpreendimentoModel, CicloEmpreendimentoId> {

    List<CicloEmpreendimentoModel> findByCicCod(Long cicCod);

    List<CicloEmpreendimentoModel> findByEmpCod(Long empCod);

    void deleteByCicCod(Long cicCod);

    boolean existsByCicCodAndEmpCod(Long cicCod, Long empCod);

    void deleteByCicCodAndEmpCod(Long cicCod, Long empCod);
}

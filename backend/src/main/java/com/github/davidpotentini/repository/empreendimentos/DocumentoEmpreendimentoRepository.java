package com.github.davidpotentini.repository.empreendimentos;

import com.github.davidpotentini.model.empreendimentos.DocumentoEmpreendimentoId;
import com.github.davidpotentini.model.empreendimentos.DocumentoEmpreendimentoModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentoEmpreendimentoRepository
        extends JpaRepository<DocumentoEmpreendimentoModel, DocumentoEmpreendimentoId> {

    List<DocumentoEmpreendimentoModel> findByEmpCod(Long empCod);

    void deleteByEmpCodAndArqCod(Long empCod, Long arqCod);
}

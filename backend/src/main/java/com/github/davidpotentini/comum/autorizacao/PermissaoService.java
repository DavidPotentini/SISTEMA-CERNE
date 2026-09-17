package com.github.davidpotentini.comum.autorizacao;

import com.github.davidpotentini.enums.ENivel;
import com.github.davidpotentini.enums.ERecurso;
import com.github.davidpotentini.model.papeis.PapelPermissaoModel;
import com.github.davidpotentini.repository.papeis.PapelPermissaoRepository;
import org.springframework.stereotype.Service;

/** Negar por padrão: sem papel, ou recurso sem linha na matriz {@code PAPEL_PERMISSOES}, ⇒ negado. */
@Service
public class PermissaoService {

    private final PapelPermissaoRepository papelPermissaoRepository;

    public PermissaoService(PapelPermissaoRepository papelPermissaoRepository) {
        this.papelPermissaoRepository = papelPermissaoRepository;
    }

    public boolean permite(Long papCod, ERecurso recurso, ENivel minimo) {
        if (papCod == null) {
            return false;
        }
        return papelPermissaoRepository.findByPapCodAndRecurso(papCod, recurso)
                .map(PapelPermissaoModel::getNivel)
                .map(nivel -> rank(nivel) >= rank(minimo))
                .orElse(false);
    }

    /** Um nível cobre todos os abaixo dele. */
    private int rank(ENivel nivel) {
        return switch (nivel) {
            case NENHUM -> 0;
            case LEITURA -> 1;
            case EDICAO -> 2;
            case TOTAL -> 3;
        };
    }
}

package com.github.davidpotentini.comum.autorizacao;

import com.github.davidpotentini.enums.ENivel;
import com.github.davidpotentini.enums.ERecurso;
import com.github.davidpotentini.model.papeis.PapelPermissaoModel;
import com.github.davidpotentini.repository.papeis.PapelPermissaoRepository;
import org.springframework.stereotype.Service;

/**
 * Decide se um papel atinge o nível mínimo exigido num recurso, lendo a matriz
 * {@code PAPEL_PERMISSOES} do schema do tenant ativo (resolvido pelo {@code TenantContext}).
 * Negar por padrão: sem papel ou recurso sem linha na matriz ⇒ negado.
 */
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

    /** Ordena os níveis: um nível cobre todos os abaixo dele. */
    private int rank(ENivel nivel) {
        return switch (nivel) {
            case NENHUM -> 0;
            case LEITURA -> 1;
            case EDICAO -> 2;
            case TOTAL -> 3;
        };
    }
}

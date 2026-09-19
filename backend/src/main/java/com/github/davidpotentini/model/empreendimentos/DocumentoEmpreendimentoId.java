package com.github.davidpotentini.model.empreendimentos;

import java.io.Serializable;
import java.util.Objects;

public class DocumentoEmpreendimentoId implements Serializable {

    private Long empCod;
    private Long arqCod;

    public DocumentoEmpreendimentoId() {
    }

    public DocumentoEmpreendimentoId(Long empCod, Long arqCod) {
        this.empCod = empCod;
        this.arqCod = arqCod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DocumentoEmpreendimentoId that)) {
            return false;
        }
        return Objects.equals(empCod, that.empCod) && Objects.equals(arqCod, that.arqCod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(empCod, arqCod);
    }
}

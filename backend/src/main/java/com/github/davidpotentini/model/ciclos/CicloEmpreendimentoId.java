package com.github.davidpotentini.model.ciclos;

import java.io.Serializable;
import java.util.Objects;

/** Chave composta de {@link CicloEmpreendimentoModel} ({@code CIC_COD}, {@code EMP_COD}). */
public class CicloEmpreendimentoId implements Serializable {

    private Long cicCod;
    private Long empCod;

    public CicloEmpreendimentoId() {
    }

    public CicloEmpreendimentoId(Long cicCod, Long empCod) {
        this.cicCod = cicCod;
        this.empCod = empCod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CicloEmpreendimentoId that)) {
            return false;
        }
        return Objects.equals(cicCod, that.cicCod) && Objects.equals(empCod, that.empCod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cicCod, empCod);
    }
}

package com.github.davidpotentini.model.monitoramento;

import java.io.Serializable;
import java.util.Objects;

public class RodadaIncubadaId implements Serializable {

    private Long rodCod;
    private Long empCod;

    public RodadaIncubadaId() {
    }

    public RodadaIncubadaId(Long rodCod, Long empCod) {
        this.rodCod = rodCod;
        this.empCod = empCod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RodadaIncubadaId that)) {
            return false;
        }
        return Objects.equals(rodCod, that.rodCod) && Objects.equals(empCod, that.empCod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rodCod, empCod);
    }
}

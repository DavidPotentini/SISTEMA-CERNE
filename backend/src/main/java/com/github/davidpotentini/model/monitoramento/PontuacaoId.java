package com.github.davidpotentini.model.monitoramento;

import com.github.davidpotentini.enums.EEixoCerne;

import java.io.Serializable;
import java.util.Objects;

public class PontuacaoId implements Serializable {

    private Long avaCod;
    private EEixoCerne dimensao;

    public PontuacaoId() {
    }

    public PontuacaoId(Long avaCod, EEixoCerne dimensao) {
        this.avaCod = avaCod;
        this.dimensao = dimensao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PontuacaoId that)) {
            return false;
        }
        return Objects.equals(avaCod, that.avaCod) && dimensao == that.dimensao;
    }

    @Override
    public int hashCode() {
        return Objects.hash(avaCod, dimensao);
    }
}

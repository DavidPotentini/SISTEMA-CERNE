package com.github.davidpotentini.enums;

/**
 * Espelha o tipo Postgres {@code VLD_NIVEL_INCUBADORA}. O rótulo no banco contém espaço
 * ({@code 'CERNE 1'}), por isso a constante Java usa {@code _} e um rótulo à parte — a
 * ponte é feita por {@code NivelIncubadoraConverter}.
 */
public enum ENivelIncubadora {
    CERNE_1("CERNE 1");

    private final String rotulo;

    ENivelIncubadora(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }

    public static ENivelIncubadora deRotulo(String rotulo) {
        for (ENivelIncubadora nivel : values()) {
            if (nivel.rotulo.equals(rotulo)) {
                return nivel;
            }
        }
        throw new IllegalArgumentException("Nível de incubadora inválido: " + rotulo);
    }
}

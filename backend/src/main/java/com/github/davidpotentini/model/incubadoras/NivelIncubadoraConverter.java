package com.github.davidpotentini.model.incubadoras;

import com.github.davidpotentini.enums.ENivelIncubadora;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converte {@link ENivelIncubadora} ↔ o rótulo do enum Postgres ({@code 'CERNE 1'}),
 * já que o rótulo tem espaço e não pode ser o nome da constante Java.
 */
@Converter
public class NivelIncubadoraConverter implements AttributeConverter<ENivelIncubadora, String> {

    @Override
    public String convertToDatabaseColumn(ENivelIncubadora nivel) {
        return nivel == null ? null : nivel.getRotulo();
    }

    @Override
    public ENivelIncubadora convertToEntityAttribute(String rotulo) {
        return rotulo == null ? null : ENivelIncubadora.deRotulo(rotulo);
    }
}

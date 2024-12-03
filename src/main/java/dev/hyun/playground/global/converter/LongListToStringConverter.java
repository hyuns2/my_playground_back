package dev.hyun.playground.global.converter;

import jakarta.persistence.AttributeConverter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LongListToStringConverter implements AttributeConverter<List<Long>, String> {
    private static final String SPLIT_CHAR = ",";

    @Override
    public String convertToDatabaseColumn(List<Long> attribute) {
        if (attribute == null || attribute.isEmpty())
            return null;

        return attribute.stream().map(Object::toString).collect(Collectors.joining(SPLIT_CHAR));
    }

    @Override
    public List<Long> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty())
            return null;

        return Arrays.stream(dbData.split(SPLIT_CHAR)).map(Long::parseLong).toList();
    }
}

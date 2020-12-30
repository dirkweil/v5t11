package de.gedoplan.v5t11.util.persistence;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class LocalDateTimeAttributeConverter implements AttributeConverter<LocalDateTime, String> {

  @Override
  public String convertToDatabaseColumn(LocalDateTime attribute) {
    return attribute != null ? attribute.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null;
  }

  @Override
  public LocalDateTime convertToEntityAttribute(String dbData) {
    return dbData != null ? LocalDateTime.parse(dbData, DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null;
  }

}

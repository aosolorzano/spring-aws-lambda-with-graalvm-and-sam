package hiperium.city.data.function.mappers;

import hiperium.city.data.function.dto.CityResponse;
import hiperium.city.data.function.dto.RecordStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

/**
 * CityMapper is an interface used for mapping attribute values of a City object to a Map object and vice versa.
 * It provides methods for converting City objects to Map objects and Map objects to City objects.
 * This interface uses annotations from the MapStruct library for mapping.
 */
@Mapper(componentModel = "spring")
public interface CityMapper {

    /**
     * Converts a map of attribute values to a City object.
     *
     * @param itemAttributesMap A map containing attribute values of a City object.
     *                          The keys represent the column names of the City table,
     *                          and the values represent the corresponding attribute values.
     * @return A City object with the attribute values mapped from the itemAttributesMap.
     */
    @Mapping(target = "id",           expression = "java(getStringValueFromAttributesMap(itemAttributesMap, CityResponse.ID_COLUMN_NAME))")
    @Mapping(target = "name",         expression = "java(getStringValueFromAttributesMap(itemAttributesMap, CityResponse.NAME_COLUMN_NAME))")
    @Mapping(target = "status",       expression = "java(getStatusEnumFromAttributesMap(itemAttributesMap))")
    @Mapping(target = "timezone",     expression = "java(getStringValueFromAttributesMap(itemAttributesMap, CityResponse.TIMEZONE_COLUMN_NAME))")
    @Mapping(target = "httpStatus",   constant   = "200")
    @Mapping(target = "errorMessage", ignore     = true)
    CityResponse toCityResponse(Map<String, AttributeValue> itemAttributesMap);

    /**
     * Retrieves the string value associated with the given key from the attribute map.
     * If the key is present in the map, the corresponding value is returned as a string.
     * If the key is not present, null is returned.
     *
     * @param attributesMap The map containing attribute values.
     * @param key The key used to retrieve the value from the map.
     * @return The string value associated with the given key, or null if the key is not present in the map.
     */
    default String getStringValueFromAttributesMap(Map<String, AttributeValue> attributesMap, String key) {
        return attributesMap.containsKey(key) ? attributesMap.get(key).s() : null;
    }

    /**
     * Retrieves the RecordStatus enum value from the attribute map based on the specified column name.
     *
     * @param itemAttributesMap A map containing attribute values of a City object. The keys represent the column names of the City table, and the values represent the corresponding
     *  attribute values.
     * @return The RecordStatus enum value retrieved from the attributes map.
     */
    default RecordStatus getStatusEnumFromAttributesMap(Map<String, AttributeValue> itemAttributesMap) {
        return RecordStatus.valueOf(this.getStringValueFromAttributesMap(itemAttributesMap, CityResponse.STATUS_COLUMN_NAME));
    }
}

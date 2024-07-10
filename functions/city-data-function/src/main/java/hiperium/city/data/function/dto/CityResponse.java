package hiperium.city.data.function.dto;

import java.util.Optional;

/**
 * Represents a response object that contains information about a city.
 */
public record CityResponse(

    String id,
    String name,
    RecordStatus status,
    String timezone,
    Integer httpStatus,
    Optional<String> errorMessage) {

    public static final String ID_COLUMN_NAME = "id";
    public static final String NAME_COLUMN_NAME = "name";
    public static final String STATUS_COLUMN_NAME = "status";
    public static final String TIMEZONE_COLUMN_NAME = "timezone";
}

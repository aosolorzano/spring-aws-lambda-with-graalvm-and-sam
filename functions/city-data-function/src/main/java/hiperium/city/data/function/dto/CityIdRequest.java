package hiperium.city.data.function.dto;

import jakarta.validation.constraints.NotNull;

/**
 * Represents a request to retrieve information about a city using its unique identifier.
 */
public record CityIdRequest(@NotNull String id) {
}

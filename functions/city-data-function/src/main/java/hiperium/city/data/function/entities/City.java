package hiperium.city.data.function.entities;

/**
 * Represents a city with its associated information.
 * This class is used for storing city details in a database.
 */
public record City(

    String id,
    String name,
    RecordStatus status,
    String timezone,
    String country) {
}

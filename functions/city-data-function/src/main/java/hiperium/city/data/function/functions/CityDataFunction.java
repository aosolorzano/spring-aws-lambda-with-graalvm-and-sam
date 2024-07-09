package hiperium.city.data.function.functions;

import hiperium.city.data.function.dto.CityIdRequest;
import hiperium.city.data.function.entities.City;
import hiperium.city.data.function.entities.RecordStatus;
import hiperium.city.data.function.utils.AppConstants;
import hiperium.city.data.function.utils.BeanValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * Represents a function that finds a city by its identifier.
 * @apiNote The Enhanced Client has problems when used with Spring Native.
 */
public class CityDataFunction implements Function<Message<CityIdRequest>, City> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CityDataFunction.class);

    private final DynamoDbClient dynamoDbClient;

    public CityDataFunction(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    /**
     * Finds a city by its identifier.
     *
     * @return The city with the matching identifier, or null if not found.
     */
    @Override
    public City apply(Message<CityIdRequest> cityIdRequestMessage) {
        LOGGER.debug("Finding city with ID: {}", cityIdRequestMessage);
        CityIdRequest cityIdRequest = cityIdRequestMessage.getPayload();
        BeanValidationUtils.validateBean(cityIdRequest);

        HashMap<String, AttributeValue> keyToGet = new HashMap<>();
        keyToGet.put("id", AttributeValue.builder().s(cityIdRequest.id()).build());
        GetItemRequest request = GetItemRequest.builder()
            .key(keyToGet)
            .tableName(AppConstants.CITY_TABLE_NAME)
            .build();

        try {
            Map<String, AttributeValue> returnedItem = this.dynamoDbClient.getItem(request).item();
            if (Objects.nonNull(returnedItem) && !returnedItem.isEmpty()) {
                // TODO: USE MAPSTRUCT
                City city = new City(
                    returnedItem.get("id").s(),
                    returnedItem.get("name").s(),
                    RecordStatus.valueOf(returnedItem.get("status").s()),
                    returnedItem.get("timezone").s(),
                    returnedItem.get("country").s()
                );
                LOGGER.debug("City found: {}", city);
                return city;
            }
        } catch (DynamoDbException e) {
            LOGGER.error("ERROR: Couldn't find a City with ID '{}': {}", cityIdRequest.id(), e.getMessage());
        }
        return null;
    }
}

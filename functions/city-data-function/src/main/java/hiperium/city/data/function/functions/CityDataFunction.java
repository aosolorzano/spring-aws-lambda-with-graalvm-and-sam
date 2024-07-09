package hiperium.city.data.function.functions;

import hiperium.city.data.function.dto.CityIdRequest;
import hiperium.city.data.function.dto.CityResponse;
import hiperium.city.data.function.mappers.CityMapper;
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
 *
 * @apiNote The Enhanced Client has problems at runtime when used with Spring Native.
 * This is because the Enhanced Client uses reflection to create the DynamoDbClient.
 * The solution is to use the low-level client instead.
 */
public class CityDataFunction implements Function<Message<CityIdRequest>, CityResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CityDataFunction.class);

    private final CityMapper cityMapper;
    private final DynamoDbClient dynamoDbClient;

    public CityDataFunction(CityMapper cityMapper, DynamoDbClient dynamoDbClient) {
        this.cityMapper = cityMapper;
        this.dynamoDbClient = dynamoDbClient;
    }

    /**
     * Finds a city by its identifier.
     *
     * @return The city with the matching identifier, or null if not found.
     */
    @Override
    public CityResponse apply(Message<CityIdRequest> cityIdRequestMessage) {
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
                CityResponse response = this.cityMapper.toCityResponse(returnedItem);
                LOGGER.debug("City found: {}", response);
                return response;
            }
        } catch (DynamoDbException e) {
            LOGGER.error("ERROR: Couldn't find a CityResponse with ID '{}': {}", cityIdRequest.id(), e.getMessage());
        }
        return null;
    }
}

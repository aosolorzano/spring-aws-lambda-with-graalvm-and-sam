package hiperium.city.data.function.configurations;

import hiperium.city.data.function.dto.CityIdRequest;
import hiperium.city.data.function.entities.City;
import hiperium.city.data.function.functions.CityDataFunction;
import hiperium.city.data.function.utils.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.function.Function;

@Configuration(proxyBeanMethods=false)
public class FunctionsConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(FunctionsConfig.class);

    private final DynamoDbClient dynamoDbClient;

    public FunctionsConfig(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    /**
     * Creates a bean that finds a city by its identifier.
     *
     * @return The function that finds a city by its identifier.
     */
    @Bean(AppConstants.FUNCTIONAL_BEAN_NAME)
    public Function<Message<CityIdRequest>, City> findById() {
        LOGGER.debug("Configuring CityDataFunction...");
        return new CityDataFunction(this.dynamoDbClient);
    }
}

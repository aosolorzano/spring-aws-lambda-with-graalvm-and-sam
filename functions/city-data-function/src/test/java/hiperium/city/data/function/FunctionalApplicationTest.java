package hiperium.city.data.function;

import hiperium.city.data.function.common.TestContainersBase;
import hiperium.city.data.function.dto.CityIdRequest;
import hiperium.city.data.function.entities.City;
import hiperium.city.data.function.utils.AppConstants;
import hiperium.city.data.function.utils.TestsUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.function.context.FunctionCatalog;
import org.springframework.cloud.function.context.test.FunctionalSpringBootTest;
import org.springframework.messaging.Message;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@FunctionalSpringBootTest(classes = FunctionalApplication.class)
class FunctionalApplicationTest extends TestContainersBase {

    private static final String EXISTING_CITY_ID = "a0ecb466-7ef5-47bf-a1ca-12f9f9328528";

    @Autowired
    private DynamoDbClient dynamoDbClient;

    @Autowired
    private FunctionCatalog functionCatalog;

    @BeforeEach
    void init() {
        TestsUtils.waitForDynamoDbToBeReady(this.dynamoDbClient);
    }

    @Test
    @DisplayName("City found")
    void givenValidCityId_whenInvokeLambdaFunction_thenReturnCityData() {
        Function<Message<CityIdRequest>, City> cityDataFunction = this.getFunctionUnderTest();
        Message<CityIdRequest> message = TestsUtils.createMessage(new CityIdRequest(EXISTING_CITY_ID));
        City city = cityDataFunction.apply(message);
        assertThat(city).isNotNull();
    }

    @Test
    @DisplayName("City not found")
    void givenNonExistingCityId_whenInvokeLambdaFunction_thenReturnNull() {
        Function<Message<CityIdRequest>, City> cityDataFunction = this.getFunctionUnderTest();
        Message<CityIdRequest> message = TestsUtils.createMessage(new CityIdRequest("non-existing-id"));
        City city = cityDataFunction.apply(message);
        assertThat(city).isNull();
    }

    private Function<Message<CityIdRequest>, City> getFunctionUnderTest() {
        Function<Message<CityIdRequest>, City> function = this.functionCatalog.lookup(Function.class,
            AppConstants.FUNCTIONAL_BEAN_NAME);
        assertThat(function).isNotNull();
        return function;
    }
}

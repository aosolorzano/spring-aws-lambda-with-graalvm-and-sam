package hiperium.city.data.function.utils;

import hiperium.city.data.function.dto.CityIdRequest;
import hiperium.city.data.function.entities.City;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;

/**
 * NOTE: This class will not be used in the final implementation. This is only used for local testing, so this is removed
 * from the final implementation in the src/main/java/hiperium/city/data/function/components/LambdaInvoker.java file.
 * <p>
 * The LambdaInvokerUtil class is responsible for invoking a Lambda function by passing a CityIdRequest message
 * and returning a City object.
 */
@Component
@Profile("local")
public class LambdaInvokerUtil implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(LambdaInvokerUtil.class);

    private final Function<Message<CityIdRequest>, City> cityDataFunction;

    public LambdaInvokerUtil(Function<Message<CityIdRequest>, City> cityDataFunction) {
        this.cityDataFunction = cityDataFunction;
    }

    /**
     * Runs the LambdaInvokerUtil by invoking the Lambda function.
     *
     * @param args The command line arguments.
     */
    @Override
    public void run(String... args) {
        LOGGER.debug("Invoking Lambda function");
        Message<CityIdRequest> message = getCityIdRequestMessage();
        this.cityDataFunction.apply(message);
    }

    private static Message<CityIdRequest> getCityIdRequestMessage() {
        return new Message<>() {
            @NonNull
            @Override
            public CityIdRequest getPayload() {
                return new CityIdRequest("a0ecb466-7ef5-47bf-a1ca-12f9f9328528");
            }
            @NonNull
            @Override
            public MessageHeaders getHeaders() {
                return new MessageHeaders(Map.of());
            }
        };
    }
}

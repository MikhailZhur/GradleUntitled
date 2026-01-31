package assertions.conditions;

import assertions.Condition;
import io.restassured.response.ValidatableResponse;
import models.swagger.Info;
import org.junit.jupiter.api.Assertions;

import static org.hamcrest.Matchers.equalTo;

public class MessageCondition implements Condition {
    private final String expectedMessage;

    public MessageCondition(String expectedMessage) {
        this.expectedMessage = expectedMessage;
    }

    @Override
    public void check(ValidatableResponse response) {
        Info info = response.extract().jsonPath().getObject("info", Info.class);
        Assertions.assertEquals(expectedMessage, info.getMessage());

        //response.body("info.message", equalTo(expectedMessage));
    }
}

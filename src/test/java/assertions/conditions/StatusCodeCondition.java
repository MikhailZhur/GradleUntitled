package assertions.conditions;

import assertions.AssertableResponse;
import assertions.Condition;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.Assertions;

public class StatusCodeCondition implements Condition {
    private final Integer statusCode;

    public StatusCodeCondition(Integer statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public void check(ValidatableResponse response) {
        int actualStatus = response.extract().statusCode();
        Assertions.assertEquals(statusCode,actualStatus);
     //   response.assertThat().statusCode(statusCode);
    }
}

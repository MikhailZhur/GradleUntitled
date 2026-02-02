package tests.swaggertests;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import models.swagger.FullUser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import services.UserService;

import java.util.List;
import static assertions.Conditions.hasMessage;
import static assertions.Conditions.hasStatusCode;
import static utils.RandomTestData.*;

public class UserNewTests {
    private static UserService userService;

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "http://85.192.34.140:8080";
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter(), new AllureRestAssured());
        userService = new UserService();
    }

    @Test
    public void positiveRegisterTest() {
        FullUser user = getRandomUser();
        userService.register(user)
                .should(hasStatusCode(201))
                .should(hasMessage("User created"));
    }

    @Test
    public void positiveRegisterWithGamesTest() {
        FullUser user = getRandomUserWithGame();
        userService.register(user)
                .should(hasStatusCode(201))
                .should(hasMessage("User created"));
    }



    @Test
    public void negativeRegisterLoginExistTest() {
        FullUser user = getRandomUser();
        userService.register(user);
        userService.register(user)
                .should(hasStatusCode(400))
                .should(hasMessage("Login already exist"));
    }

    @Test
    public void registerUserNoPasswordTest() {
        FullUser user = getRandomUser();
        user.setPass(null);

        userService.register(user)
                .should(hasStatusCode(400))
                .should(hasMessage("Missing login or password"));
    }


    @Test
    public void positiveAdminAuthTest() {
        FullUser user = getAdminUser();
        String token = userService.auth(user)
                .should(hasStatusCode(200))
                .asJwt();
        Assertions.assertNotNull(token);
    }


    @Test
    public void positiveNewUserAuthTest() {
        FullUser user = getRandomUser();
        userService.register(user);

        String token = userService.auth(user)
                .should(hasStatusCode(200))
                .asJwt();
        Assertions.assertNotNull(token);
    }

    @Test
    public void negativeAuthTest() {

        FullUser user = getRandomUser();
        userService.auth(user)
                .should(hasStatusCode(401));
    }

    @Test
    public void positiveGetUserInfoTest() {
        FullUser user = getAdminUser();
        String token = userService.auth(user).asJwt();
        userService.getUserInfo(token)
                .should(hasStatusCode(200));
    }

    @Test
    public void negativeGetUserInfoInvalidJWTTest() {
        userService.getUserInfo("Fakejjjwt").should(hasStatusCode(401));
    }

    @Test
    public void negativeGetUserInfoWithOutJwtTTest() {
        userService.getUserInfo().should(hasStatusCode(401));
    }

    @Test
    public void positiveUpdatePasswordUserTest() {
        FullUser user = getRandomUser();
        String oldPassword = user.getPass();
        userService.register(user);

        String token = userService.auth(user).asJwt();
        Assertions.assertNotNull(token);

        String updatePassValue = "new123Qwe";

        userService.updatePass(updatePassValue, token)
                .should(hasStatusCode(200))
                .should(hasMessage("User password successfully changed"));

        user.setPass(updatePassValue);

        token = userService.auth(user).should(hasStatusCode(200)).asJwt();

        FullUser updatedUser = userService.getUserInfo(token).as(FullUser.class);

        Assertions.assertNotEquals(oldPassword, updatedUser.getPass());
    }

    @Test
    public void negativeChangeAdminPasswordTest() {
        FullUser user = getAdminUser();
        String token = userService.auth(user).asJwt();

        Assertions.assertNotNull(token);

        String updatePassValue = "new123Qwe";
        userService.updatePass(updatePassValue, token)
                .should(hasStatusCode(400))
                .should(hasMessage("Cant update base users"));
    }

    @Test
    public void negativeDeleteAdminTest() {
        FullUser user = getAdminUser();

        String token = userService.auth(user).asJwt();

        userService.deleteUser(token)
                .should(hasStatusCode(400))
                .should(hasMessage("Cant delete base users"));
    }

    @Test
    public void positiveDeleteUserTest() {
        FullUser user = getRandomUser();
        userService.register(user);
        String token = userService.auth(user).asJwt();
        userService.deleteUser(token)
                .should(hasStatusCode(200))
                .should(hasMessage("User successfully deleted"));
    }

    @Test
    public void positiveGetAllUsersTest() {
        List<String> users = userService.getAllUsers().asList(String.class);
        Assertions.assertTrue(users.size() >= 3);
    }
}

package tests.swaggertests;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import listener.AdminUser;
import listener.AdminUserResolver;
import models.swagger.FullUser;
import models.swagger.Info;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import services.UserService;

import java.util.List;
import static assertions.Conditions.hasMessage;
import static assertions.Conditions.hasStatusCode;
import static utils.RandomTestData.*;

@ExtendWith(AdminUserResolver.class)

public class UserNewTests {
    private static UserService userService;
    private FullUser user;

    @BeforeEach
    public void initTestUser(){
        user = getRandomUser();
    }

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "http://85.192.34.140:8080";
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter(), new AllureRestAssured());
        userService = new UserService();
    }

    @Test
    public void positiveRegisterTest() {
        userService.register(user)
                .should(hasStatusCode(201))
                .should(hasMessage("User created"));
    }

    @Test
    public void positiveRegisterWithGamesTest() {
        FullUser user = getRandomUserWithGame();
        Response response = userService.register(user)
      //          .should(hasStatusCode(201))
      //          .should(hasMessage("User created"))
                .asResponse();
        Info info = response.jsonPath().getObject("info", Info.class);

        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(info.getMessage()).as("Сообщение об ошибке было не верное")
                .isEqualTo("фейк месс");
        softAssertions.assertThat(response.statusCode()).as("Статус код был не 200")
                .isEqualTo(201);
        softAssertions.assertAll();
    }



    @Test
    public void negativeRegisterLoginExistTest() {
        userService.register(user);
        userService.register(user)
                .should(hasStatusCode(400))
                .should(hasMessage("Login already exist"));
    }

    @Test
    public void registerUserNoPasswordTest() {
        user.setPass(null);

        userService.register(user)
                .should(hasStatusCode(400))
                .should(hasMessage("Missing login or password"));
    }


    @Test
    public void positiveAdminAuthTest(@AdminUser FullUser admin) {

        String token = userService.auth(admin)
                .should(hasStatusCode(200))
                .asJwt();

        Assertions.assertNotNull(token);
    }


    @Test
    public void positiveNewUserAuthTest() {
        userService.register(user);

        String token = userService.auth(user)
                .should(hasStatusCode(200))
                .asJwt();
        Assertions.assertNotNull(token);
    }

    @Test
    public void negativeAuthTest() {
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

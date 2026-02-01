package tests.swaggertests;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import models.swagger.FullUser;
import models.swagger.Info;
import models.swagger.JwtAuthData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import services.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static assertions.Conditions.hasMessage;
import static assertions.Conditions.hasStatusCode;
import static io.restassured.RestAssured.given;

public class UserNewTests {
    public static Random random;
    private static UserService userService;

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "http://85.192.34.140:8080";
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter(), new AllureRestAssured());
        random = new Random();
        userService = new UserService();
    }

    private FullUser getRandomUser() {
        int randomNumber = Math.abs(random.nextInt(5000));
        return FullUser.builder()
                .login("ThredQaUser" + randomNumber)
                .pass("123qwe")
                .build();
    }

    private FullUser getAdminUser() {
        return FullUser.builder()
                .login("admin")
                .pass("admin")
                .build();
    }


    @Test
    public void positiveRegisterTest() {
        FullUser user = getRandomUser();
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
        int randomNumber = Math.abs(random.nextInt(5000));
        FullUser user = FullUser.builder()
                .login("ThredQaUser" + randomNumber)
                .pass("123qwe")
                .build();

        given()
                .contentType(ContentType.JSON)
                .body(user)
                .post("/api/signup")
                .then()
                .statusCode(201);

        JwtAuthData authData = new JwtAuthData(user.getLogin(), user.getPass());

        String token = given()
                .contentType(ContentType.JSON)
                .body(authData)
                .post("/api/login")
                .then()
                .statusCode(200)
                .extract().jsonPath().getString("token");

        Assertions.assertNotNull(token);

        Map<String, String> password = new HashMap<>();
        String updatePassValue = "new123Qwe";
        password.put("password", updatePassValue);

        Info updatePassInfo = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(token)
                .body(password)
                .put("/api/user")
                .then()
                .extract().jsonPath().getObject("info", Info.class);

        Assertions.assertEquals("User password successfully changed", updatePassInfo.getMessage());

        authData.setPassword(updatePassValue);

        token = given()
                .contentType(ContentType.JSON)
                .body(authData)
                .post("/api/login")
                .then()
                .statusCode(200)
                .extract().jsonPath().getString("token");

        FullUser updatedUser = given()
                .auth().oauth2(token)
                .get("/api/user")
                .then()
                .statusCode(200)
                .extract().as(FullUser.class);

        Assertions.assertNotEquals(user.getPass(), updatedUser.getPass());
    }

    @Test
    public void negativeChangeAdminPasswordTest() {

        JwtAuthData authData = new JwtAuthData("admin", "admin");

        String token = given()
                .contentType(ContentType.JSON)
                .body(authData)
                .post("/api/login")
                .then()
                .statusCode(200)
                .extract().jsonPath().getString("token");

        Assertions.assertNotNull(token);

        Map<String, String> password = new HashMap<>();
        String updatePassValue = "new123Qwe";
        password.put("password", updatePassValue);

        Info updatePassInfo = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(token)
                .body(password)
                .put("/api/user")
                .then()
                .statusCode(400)
                .extract().jsonPath().getObject("info", Info.class);

        Assertions.assertEquals("Cant update base users", updatePassInfo.getMessage());
    }

    @Test
    public void negativeDeleteAdminTest() {
        JwtAuthData authData = new JwtAuthData("admin", "admin");

        String token = given()
                .contentType(ContentType.JSON)
                .body(authData)
                .post("/api/login")
                .then()
                .statusCode(200)
                .extract().jsonPath().getString("token");

        Info deleteAdmin = given()
                .auth().oauth2(token)
                .delete("/api/user")
                .then().statusCode(400)
                .extract().jsonPath().getObject("info", Info.class);

        Assertions.assertEquals("Cant delete base users", deleteAdmin.getMessage());

    }

    @Test
    public void positiveDeleteAdminTest() {
        int randomNumber = Math.abs(random.nextInt(5000));
        FullUser user = FullUser.builder()
                .login("ThredQaUser" + randomNumber)
                .pass("123qwe")
                .build();

        given()
                .contentType(ContentType.JSON)
                .body(user)
                .post("/api/signup")
                .then()
                .statusCode(201);

        JwtAuthData authData = new JwtAuthData(user.getLogin(), user.getPass());

        String token = given()
                .contentType(ContentType.JSON)
                .body(authData)
                .post("/api/login")
                .then()
                .statusCode(200)
                .extract().jsonPath().getString("token");

        Info infoDelete = given()
                .auth().oauth2(token)
                .delete("/api/user")
                .then().statusCode(200)
                .extract().jsonPath().getObject("info", Info.class);

        Assertions.assertEquals("User successfully deleted", infoDelete.getMessage());
    }

    @Test
    public void positiveGetAllUsersTest() {
        List<String> users = given()
                .get("/api/users")
                .then().extract().as(new TypeRef<>() {
                });

        Assertions.assertTrue(users.size() >= 3);

    }
}

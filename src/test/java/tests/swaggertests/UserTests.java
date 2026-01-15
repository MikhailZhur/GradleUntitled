package tests.swaggertests;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import models.swagger.FullUser;
import models.swagger.Info;
import models.swagger.JwtAuthData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static io.restassured.RestAssured.given;


public class UserTests {
    public static Random random;

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "http://85.192.34.140:8080";
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter(), new AllureRestAssured());
        random = new Random();
    }

    @Test
    public void positiveRegisterTest() {
        int randomNumber = Math.abs(random.nextInt(5000));
        FullUser user = FullUser.builder()
                .login("ThredQaUser" + randomNumber)
                .pass("123qwe")
                .build();

        Info info = given()
                .contentType(ContentType.JSON)
                .body(user)
                .post("/api/signup")
                .then()
                .statusCode(201)
                .extract().jsonPath().getObject("info", Info.class);

        Assertions.assertEquals("success", info.getStatus());
        Assertions.assertEquals("User created", info.getMessage());
    }

    @Test
    public void negativeRegisterLoginExistTest() {
        int randomNumber = Math.abs(random.nextInt(5000));
        FullUser user = FullUser.builder()
                .login("ThredQaUser" + randomNumber)
                .pass("123qwe")
                .build();

        Info info = given()
                .contentType(ContentType.JSON)
                .body(user)
                .post("/api/signup")
                .then()
                .statusCode(201)
                .extract().jsonPath().getObject("info", Info.class);

        Info ErrorInfo = given()
                .contentType(ContentType.JSON)
                .body(user)
                .post("/api/signup")
                .then()
                .statusCode(400)
                .extract().jsonPath().getObject("info", Info.class);

        Assertions.assertEquals("Login already exist", ErrorInfo.getMessage());
    }

    @Test
    public void registerUserNoPasswordTest() {
        int randomNumber = Math.abs(random.nextInt(5000));
        FullUser user = FullUser.builder()
                .login("ThredQaUser" + randomNumber)
                .build();

        Info info = given()
                .contentType(ContentType.JSON)
                .body(user)
                .post("/api/signup")
                .then()
                .statusCode(400)
                .extract().jsonPath().getObject("info", Info.class);

        Assertions.assertEquals("fail", info.getStatus());
        Assertions.assertEquals("Missing login or password", info.getMessage());

    }

    @Test
    public void positiveAdminAuthTest(){
        JwtAuthData authData = new JwtAuthData("admin","admin");

         String token = given()
                .contentType(ContentType.JSON)
                 .body(authData)
                .post("/api/login")
                .then()
                .statusCode(200)
                .extract().jsonPath().getString("token");

        Assertions.assertNotNull(token);
    }

    @Test
    public void positiveNewUserAuthTest(){
        int randomNumber = Math.abs(random.nextInt(5000));
        FullUser user = FullUser.builder()
                .login("ThredQaUser" + randomNumber)
                .pass("123qwe")
                .build();

        Info info = given()
                .contentType(ContentType.JSON)
                .body(user)
                .post("/api/signup")
                .then()
                .statusCode(201)
                .extract().jsonPath().getObject("info", Info.class);

        Assertions.assertEquals("success", info.getStatus());
        Assertions.assertEquals("User created", info.getMessage());

        JwtAuthData authData = new JwtAuthData(user.getLogin(), user.getPass());

        String token = given()
                .contentType(ContentType.JSON)
                .body(authData)
                .post("/api/login")
                .then()
                .statusCode(200)
                .extract().jsonPath().getString("token");

        Assertions.assertNotNull(token);
    }

    @Test
    public void negativeAuthTest(){

        JwtAuthData authData = new JwtAuthData("user22", "ghtr454");

        given()
                .contentType(ContentType.JSON)
                .body(authData)
                .post("/api/login")
                .then()
                .statusCode(401);
    }
}

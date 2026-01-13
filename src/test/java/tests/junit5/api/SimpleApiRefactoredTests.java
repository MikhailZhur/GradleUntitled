package tests.junit5.api;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import models.fakeapiuser.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

public class SimpleApiRefactoredTests {
    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "https://fakestoreapi.com";
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter(), new AllureRestAssured());
    }

    @Test
    public void getAllUserTest() {
        given().get("/users")
                .then()
                .statusCode(200);
    }

    @Test
    public void getSingleUserTest() {
        int userId = 2;
        UserRoot response = given()
                .pathParam("userId", userId)
                .get("/users/{userId}")
                .then()
                .statusCode(200)
                .extract().as(UserRoot.class);

        Name name = given()
                .pathParam("userId", userId)
                .get("/users/{userId}")
                .then()
                .statusCode(200)
                .extract().jsonPath().getObject("name", Name.class);

        Assertions.assertEquals(userId, response.getId());
        Assertions.assertTrue(response.getAddress().getZipCode().matches("\\d{5}-\\d{4}"));
    }


    @Test
    public void getAllUsersWithLimitTest() {
        int limitSize = 3;
        List<UserRoot> users = given().queryParam("limit", limitSize)
                .get("/users")
                .then()
                .statusCode(200)
                .extract().as(new TypeRef<List<UserRoot>>() {
                });

        Assertions.assertEquals(3, users.size());
    }


    @ParameterizedTest
    @ValueSource(ints = {-1, 1, 10})
    public void getAllUsersWithLimitParametrizedTest(int limitSize) {
        List<UserRoot> users = given().queryParam("limit", limitSize)
                .get("/users")
                .then()
                .statusCode(200)
                .extract().as(new TypeRef<List<UserRoot>>() {
                });

        Assertions.assertEquals(limitSize, users.size());
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 40})
    public void getAllUsersWithLimitWithErrorParamsTest(int limitSize) {
        List<UserRoot> users = given().queryParam("limit", limitSize)
                .get("/users")
                .then()
                .statusCode(200)
                .extract().as(new TypeRef<List<UserRoot>>() {
                });

        Assertions.assertNotEquals(limitSize, users.size());
    }


    @Test
    public void getAllUsersSortByDescTest() {
        String sortType = "desc";
        List<UserRoot> userSorted = given().queryParam("sort", sortType)
                .get("/users")
                .then()
                .extract().as(new TypeRef<List<UserRoot>>() {
                });

        List<UserRoot> usersNotSorted = given()
                .get("/users")
                .then()
                .extract().as(new TypeRef<List<UserRoot>>() {
                });

        List<Integer> sortedResponseIds = userSorted
                .stream()
                .map(UserRoot::getId).collect(Collectors.toList());

        List<Integer> notSortedResponseIds = usersNotSorted
                .stream()
                .map(UserRoot::getId).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        ;


        Assertions.assertNotEquals(userSorted, usersNotSorted);
        Assertions.assertEquals(sortedResponseIds, notSortedResponseIds);
    }

    private UserRoot getTestUser() {
        Random random = new Random();
        Name name = new Name("Thomas", "Anderson");
        Geolocation geolocation = new Geolocation("2132", "23545");
        Address address = Address.builder()
                .geolocation(geolocation)
                .city("Moskow")
                .number(random.nextInt(100))
                .zipCode("2332-4574").build();

        return UserRoot.builder()
                .id(6)
                .name(name)
                .phone("223432")
                .email("gldsgjl@mail")
                .username("ghomas")
                .password("123qwe")
                .address(address).build();
    }

    @Test
    public void addNewUserTest() {
        UserRoot user = getTestUser();

        Integer userId = given()
                .body(user)
                .post("/users")
                .then()
                .statusCode(201)
                .extract().jsonPath().getInt("id");
        Assertions.assertNotNull(userId);
    }

    @Test
    public void updateUserTest() {
        UserRoot user = getTestUser();
        String odlPassword = user.getPassword();
        user.setPassword(("newPass"));

        UserRoot updatedUser = given()
                .body(user)
                .pathParam("userId", user.getId())
                .put("/users/{userId}")
                .then().extract().as(UserRoot.class);

        Assertions.assertNotEquals(updatedUser.getPassword(), odlPassword);
    }

    @Test
    public void authUserTest() {
        AuthData authData = new AuthData("jimmie_k", "klein*#%*");

        String token = given()
                .contentType(ContentType.JSON)
                .body(authData)
                .post("/auth/login")
                .then()
                .statusCode(201)
                .extract().jsonPath().getString("token");

        Assertions.assertNotNull(token);
    }
}

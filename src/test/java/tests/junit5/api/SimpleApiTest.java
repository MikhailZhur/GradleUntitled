package tests.junit5.api;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import models.fakeapiuser.Address;
import models.fakeapiuser.Geolocation;
import models.fakeapiuser.Name;
import models.fakeapiuser.UserRoot;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class SimpleApiTest {

    @Test
    public void getAllUserTest() {
        given().get("https://fakestoreapi.com/users")
                .then()
                .log().all()
                .statusCode(200);
    }

    @Test
    public void getSingleUserTest() {
        int userId = 2;
        given().pathParam("userId", userId)
                .get("https://fakestoreapi.com/users/{userId}")
                .then().log().all()
                .body("id", equalTo(userId))
                .body("address.zipcode", matchesPattern("\\d{5}-\\d{4}"));
    }

    @Test
    public void getAllUsersWithLimitTest() {
        int limitSize = 3;
        given().queryParam("limit", limitSize)
                .get("https://fakestoreapi.com/users/")
                .then().log().all()
                .statusCode(200)
                .body("", hasSize(greaterThanOrEqualTo(limitSize)));
    }

    @Test
    public void getAllUsersSortByDescTest() {
        String sortType = "desc";
        Response sortedResponse = given().queryParam("sort", sortType)
                .get("https://fakestoreapi.com/users/")
                .then().log().all()
                .extract().response();

        Response notSortedResponse = given()
                .get("https://fakestoreapi.com/users/")
                .then().log().all().extract().response();

        List<Integer> sortedResponseIds = sortedResponse.jsonPath().getList("id");
        List<Integer> notSortedResponseIds = notSortedResponse.jsonPath().getList("id");

        List<Integer> sortedByCode = notSortedResponseIds
                .stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        Assertions.assertNotEquals(sortedResponseIds, notSortedResponseIds);
        Assertions.assertEquals(sortedByCode, sortedResponseIds);
    }

    @Test
    public void addNewUserTest(){
        Name name = new Name("Thomas","Anderson");
        Geolocation geolocation = new Geolocation("2132","23545");
        Address address = Address.builder()
                .geolocation(geolocation)
                .city("Moskow")
                .number(4)
                .zipCode("2332-4574").build();

        UserRoot bodyRequest = UserRoot.builder()
                .name(name)
                .phone("223432")
                .email("gldsgjl@mail")
                .username("ghomas")
                .password("123qwe")
                .address(address).build();

        given()
                .body(bodyRequest)
                .post("https://fakestoreapi.com/users")
                .then().log().all()
                .statusCode(201)
                .body("id", notNullValue());

    }

    private UserRoot getTestUser(){
        Name name = new Name("Thomas","Anderson");
        Geolocation geolocation = new Geolocation("2132","23545");
        Address address = Address.builder()
                .geolocation(geolocation)
                .city("Moskow")
                .number(4)
                .zipCode("2332-4574").build();

        return UserRoot.builder()
                .name(name)
                .phone("223432")
                .email("gldsgjl@mail")
                .username("ghomas")
                .password("123qwe")
                .address(address).build();
    }

    @Test
    public void updateUserTest(){
        UserRoot user = getTestUser();
        String odlPassword = user.getPassword();

        user.setPassword(("newPass"));
        given().body(user)
                .put("https://fakestoreapi.com/users/" + user.getId())
                .then().log().all()
                .body("password", not(equalTo(odlPassword)));

        System.out.println(odlPassword);
        System.out.println(user.getPassword());
    }

    @Test
    public void deleteUserTest(){
        given()
                .delete("https://fakestoreapi.com/users/7")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    public void authUserTest(){
        Map<String ,String> userAuth = new HashMap<>();
        userAuth.put("username", "jimmie_k");
        userAuth.put("password", "klein*#%*");

        given().log().all()
                .contentType(ContentType.JSON)
                .body(userAuth)
                .post("https://fakestoreapi.com/auth/login")
                .then().log().all()
                .statusCode(201)
                .body("token", notNullValue());
    }

}

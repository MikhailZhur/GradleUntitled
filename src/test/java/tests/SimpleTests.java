package tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import models.Cat;
import models.People;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.File;
import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Tag("Api")

public class SimpleTests {


    @Test
    public void test() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File("src/test/resources/stas.json");
        People people = objectMapper.readValue(file, People.class);
        System.out.println(people.getName());
        System.out.println(people.getAge());
        System.out.println(people.getSex());

        People sasha = new People("sasha", 10, "female");
        String json = objectMapper.writeValueAsString(sasha);
        System.out.println(json);
    }

    @Test
    public void test1(){
        Cat cat = new Cat("Tikhon", "British");
        System.out.println(cat);

        Cat cat1 = new Cat();
    }

    @Test
    public void getAllUserTests() {
        given().get("https://fakestoreapi.com/users")
                .then()
                .log().all()
                .statusCode(200);
    }

    @Test
    void getSingleUserTests() {
        int userId = 5;
        given().pathParam("userId",userId)
        .get("https://fakestoreapi.com/users/{userId}")
                .then()
                .log().all()
                .statusCode(200)
                .body("id", equalTo(userId))
                .body("address.zipcode", matchesPattern("\\d{5}-\\d{4}"));
    }

    @Test
    void getAllUserWithLimitTests() {
        int limitSize = 3;
        given().queryParam("limit", limitSize)
                .get("https://fakestoreapi.com/users")
                .then().log().all()
                .body("", hasSize(limitSize));

    }

}

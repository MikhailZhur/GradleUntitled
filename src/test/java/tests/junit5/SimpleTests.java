package tests.junit5;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import listener.RetryListener;
import models.Cat;
import models.People;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import utils.JsonHelper;

import java.io.File;
import java.io.IOException;

@Tag("API")
@ExtendWith(RetryListener.class)
public class SimpleTests {

    @AfterAll
    public static void saveFailed(){
        RetryListener.saveFailedTests();
    }

    private static int age = 0;

    @Test
    public void fasi(){
        int b = 6;
        age++;
        Assertions.assertEquals(2,age);
    }

    @Test
    @DisplayName("Результат сравнения")
    public void testTwoLessThanThree(){

        int a = 4;
        int b = 6;
        Assertions.assertTrue(b>a, "Число а " + b + " больше, чем число " + a);
    }

    @ParameterizedTest
    @CsvSource({"stas, 18, male", "antons, 20, male", "sasha, 90, male"})
    public void testTwoLessThanFour(String name, String age, String sex){
        System.out.println(name + " " + age + " " + sex);
        Assertions.assertTrue(name.contains("s"));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/people.csv")
    public void testTwoLessThanFive(String name, String age, String sex){
        System.out.println(name + " " + age + " " + sex);
        Assertions.assertTrue(name.contains("s"));
    }

    @Test
    @DisplayName("Тест с jackson")
    public void testJackson() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File("src/test/resources/stas.json");
        People people = objectMapper.readValue(file, People.class);
        System.out.println(people.getName());
        System.out.println(people.getAge());
        System.out.println(people.getSex());

        People nina = new People("Nina", 66, "female");
        String json = objectMapper.writeValueAsString(nina);
        System.out.println(json);

    }

    @Test
    @DisplayName("Тест с использованием JsonHelper")
    public void testJsonHelper() throws JsonProcessingException {
        Cat cat = JsonHelper.fromJson("src/test/resources/cat.json", Cat.class);
        People people = JsonHelper.fromJson("src/test/resources/stas.json", People.class);
        System.out.println(cat);
        System.out.println(people);

        System.out.println(JsonHelper.toJson(cat));
    }

    @Test
    public void catTest(){
        Cat cat = Cat.builder()
                .name("Nolan")
                .model("British")
                .age(34)
                .build();
        System.out.println(cat);
        int realCatAge = cat.getAge()+10;
        System.out.println(cat);
        System.out.println(realCatAge);
    }

}

package tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Cat;
import models.People;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import utils.JsonHelper;

import java.io.File;
import java.io.IOException;

public class SimpleTests {
    
    @Test
    @DisplayName("Результат сравнения")
    public void testTwoLessThanThree(){

        int a = 2;
        int b = 3;
        Assertions.assertTrue(b>a, "Число а " + a + " больше, чем число " + b);
    }

    @ParameterizedTest
    @CsvSource({"stas, 18, male", "anton, 20, male", "sasha, 90, male"})
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

}

package ui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.time.Duration;

public class SeleniumTests {
    private final WebDriver driver = new ChromeDriver();;


    @BeforeEach
    public void setUp(){
        driver.manage().window().setSize(new Dimension(1920,1080));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(20));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));

    }

    @AfterEach
    public void tearDown(){
        driver.close();
    }


    @Test
    public void simpleFormTests(){
        String expectedName = "Zhuravlev Aleksey";
        String expectedEmail = "miha99_66@mail.ru";
        String expectedCurrentAddress = "Novik";
        String expectedPermanentAddress = "Kras";

        driver.get("http://85.192.34.140:8081/");
        WebElement elementsCard = driver.findElement(By.xpath("//div[@class='card-body']//h5[text()='Elements']"));
        elementsCard.click();

        WebElement elementsTextBox = driver.findElement(By.xpath("//span[text()='Text Box']"));
        elementsTextBox.click();

        WebElement fullName = driver.findElement(By.id("userName"));
        WebElement email = driver.findElement(By.id("userEmail"));
        WebElement currentAddress = driver.findElement(By.id("currentAddress"));
        WebElement permanentAddress = driver.findElement(By.id("permanentAddress"));
        WebElement submitButton = driver.findElement(By.id("submit"));


        fullName.sendKeys(expectedName);
        email.sendKeys(expectedEmail);
        currentAddress.sendKeys(expectedCurrentAddress);
        permanentAddress.sendKeys(expectedPermanentAddress);
        submitButton.click();

        WebElement fullNameNew = driver.findElement(By.xpath("//div[@id ='output']//p[@id ='name']"));
        WebElement emailNew = driver.findElement(By.xpath("//div[@id ='output']//p[@id ='email']"));
        WebElement currentAddressNew = driver.findElement(By.xpath("//div[@id ='output']//p[@id ='currentAddress']"));
        WebElement permanentAddressNew = driver.findElement(By.xpath("//div[@id ='output']//p[@id ='permanentAddress']"));

        String actualName = fullNameNew.getText();
        String actualEmail = emailNew.getText();
        String actualCurrentAddress = currentAddressNew.getText();
        String actualPermanentAddress = permanentAddressNew.getText();

        Assertions.assertTrue(actualName.contains(expectedName));
        Assertions.assertTrue(actualEmail.contains(expectedEmail));
        Assertions.assertTrue(actualCurrentAddress.contains(expectedCurrentAddress));
        Assertions.assertTrue(actualPermanentAddress.contains(expectedPermanentAddress));
    }
}

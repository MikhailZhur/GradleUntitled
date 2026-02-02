package tests.swaggertests;

import io.qameta.allure.Attachment;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import services.FileService;

import java.io.File;

import static assertions.Conditions.hasMessage;
import static assertions.Conditions.hasStatusCode;

public class FileTests {
    private static FileService fileService;

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "http://85.192.34.140:8080";
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter(), new AllureRestAssured());
        fileService = new FileService();
    }

    @Attachment(value = "downLoaded", type = "image/png")
    private byte[] attachFile(byte[] bytes) {
        return bytes;
    }

    @Test
    public void positiveDownloadTest() {
        byte[] file = fileService.downLoadBaseImage().asResponse().asByteArray();
        attachFile(file);
        File expectedFile = new File("src/test/resources/threadqa.jpeg");

        Assertions.assertEquals(expectedFile.length(), file.length);
    }

    @Test
    public void positiveUploadTest() {
        File expectedFile = new File("src/test/resources/threadqa.jpeg");
        fileService.uploadFile(expectedFile)
                .should(hasStatusCode(200))
                .should(hasMessage("file uploaded to server"));

        byte[] actualFile = fileService.downLoadLastImage().asResponse().asByteArray();
        Assertions.assertTrue(actualFile.length != 0);
        Assertions.assertEquals(expectedFile.length(), actualFile.length);
    }

}

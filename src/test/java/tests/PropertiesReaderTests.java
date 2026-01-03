package tests;

import lombok.SneakyThrows;
import models.Settings;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import utils.AppConfig;
import utils.JsonHelper;

import java.io.FileInputStream;
import java.util.Properties;

public class PropertiesReaderTests {

    @Test
    @DisplayName("Чтение настроек из файла")
    @SneakyThrows
    public void SimpleReaderTest(){
        Properties properties = new Properties();
        FileInputStream fs = new FileInputStream("src/test/resources/project.properties");
        properties.load(fs);

        String url = properties.getProperty("url");
        boolean isProduction = Boolean.parseBoolean(properties.getProperty("is_production"));
        int threads = Integer.parseInt(properties.getProperty("thread"));

        System.out.println(url);
        System.out.println(isProduction);
        System.out.println(threads);
    }

    @Test
    @DisplayName("Чтение настроек с помощью jackson")
    @SneakyThrows
    public void jacksonReaderTest(){
        Properties properties = new Properties();
        FileInputStream fs = new FileInputStream("src/test/resources/project.properties");
        properties.load(fs);

        String json = JsonHelper.toJson(properties);
        System.out.println(json);

        Settings settings = JsonHelper.fromJsonString(json, Settings.class);
        System.out.println(settings.getUrl());
        System.out.println(settings.getIsProduction());
        System.out.println(settings.getThread());

    }

    @Test
    @DisplayName("Чтение настроек при помощи библиотеки Owner")
    public void ownerReaderTest(){
        AppConfig appConfig = ConfigFactory.create(AppConfig.class);
        System.out.println(appConfig.url());
        System.out.println(appConfig.isProduction());
        System.out.println(appConfig.thread());
    }

}

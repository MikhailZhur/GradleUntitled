package utils;

import com.github.javafaker.Faker;
import models.swagger.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class RandomTestData {

    private static Random random = new Random();

    private static Faker faker = new Faker();

    public static FullUser getRandomUserWithGame() {
        int randomNumber = Math.abs(random.nextInt());

        SimilarDlc similarDlc = SimilarDlc.builder()
                .isFree(false)
                .dlcNameFromAnotherGame(faker.funnyName().name())
                .build();

        DlcItem dlcsItem = DlcItem.builder()
                .rating(faker.random().nextInt(10))
                .price(faker.random().nextInt(1, 500))
                .description(faker.funnyName().name())
                .dlcName(faker.dragonBall().character())
                .isDlcFree(false)
                .similarDlc(similarDlc).build();


        Requirements requirements = Requirements.builder()
                .ramGb(faker.random().nextInt(4, 16))
                .osName("Windows")
                .hardDrive(faker.random().nextInt(30, 70))
                .videoCard("NVIDEA")
                .build();

        GamesItem gamesItem = GamesItem.builder()
                .requirements(requirements)
                .genre(faker.book().genre())
                .price(random.nextInt(400))
                .description(faker.funnyName().name())
                .company(faker.company().name())
                .isFree(false)
                .title(faker.beer().name())
                .rating(faker.random().nextInt(10))
                .publishDate(LocalDateTime.now().toString())
                .requiredAge(random.nextBoolean())
                .tags(Arrays.asList("shooter", "quests"))
                .dlcsItem(Collections.singletonList(dlcsItem))
                .build();


        return FullUser.builder()
                .login(faker.name().username() + randomNumber)
                .pass(faker.internet().password())
                .gamesItem(Collections.singletonList(gamesItem))
                .build();
    }

    public static FullUser getRandomUser() {
        int randomNumber = Math.abs(random.nextInt(5000));
        return FullUser.builder()
                .login("ThredQaUser" + randomNumber)
                .pass("123qwe")
                .build();
    }

    public static FullUser getAdminUser() {
        return FullUser.builder()
                .login("admin")
                .pass("admin")
                .build();
    }
}


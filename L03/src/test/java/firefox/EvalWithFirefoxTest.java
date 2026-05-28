package firefox;

import finder.Eval;
import finder.Hub;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.openqa.selenium.*;
import starter.WebLauncher;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;

@Execution(ExecutionMode.SAME_THREAD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EvalWithFirefoxTest {

    private static final String START_URL = "https://auto.ru/";
    private static final String EVAL_URL = "/evaluation/cars/";
    private static final String RESULT_URL = "evaluation_id=";

    private static final WebLauncher chromeLauncher = new WebLauncher();
    private static final WebLauncher firefoxLauncher = new WebLauncher();

    private static Hub chromeHub;
    private static Hub firefoxHub;

    private static Eval chromeEval;
    private static Eval firefoxEval;

    private static final Random RANDOM = new Random();

    private static final List<String> VINS = List.of(
            "Е717СТ123",
            "JMBSNCY2ABU000820",
            "KMHFC41DP9A355518",
            "Z8NBAABD0F0030864",
            "WAUZZZ8P08A078087"
    );

    private static final List<String> CITIES = List.of(
            "Москва",
            "Санкт-Петербург",
            "Казань",
            "Екатеринбург",
            "Новосибирск",
            "Краснодар",
            "Сочи"
    );

    @BeforeAll
    public static void setUp() {
        chromeLauncher.setupChromeDriver();
        firefoxLauncher.setupFirefoxDriver();

        assertNotNull(chromeLauncher.getDriver());
        assertNotNull(firefoxLauncher.getDriver());

        chromeHub = new Hub(chromeLauncher.getDriver());
        firefoxHub = new Hub(firefoxLauncher.getDriver());

        chromeEval = new Eval(chromeLauncher.getDriver());
        firefoxEval = new Eval(firefoxLauncher.getDriver());
    }

    @Test
    @Order(1)
    @DisplayName("Оценка 1")
    public void evaluationTest1() {
        runEvaluation(VINS.get(0), mileage(), city(), true);
    }

    @Test
    @Order(2)
    @DisplayName("Оценка 2")
    public void evaluationTest2() {
        runEvaluation(VINS.get(1), mileage(), city(), true);
    }

    @Test
    @Order(3)
    @DisplayName("Оценка 3")
    public void evaluationTest3() {
        runEvaluation(VINS.get(2), mileage(), city(), true);
    }

    @Test
    @Order(4)
    @DisplayName("Оценка 4")
    public void evaluationTest4() {
        runEvaluation(VINS.get(3), mileage(), city(), true);
    }

    @Test
    @Order(5)
    @DisplayName("Оценка 5")
    public void evaluationTest5() {
        runEvaluation(VINS.get(4), mileage(), city(), true);
    }

    @Test
    @Order(6)
    @DisplayName("Пустой VIN")
    public void emptyVinTest() {
        runEvaluation("", mileage(), city(), false);
    }

    @Test
    @Order(7)
    @DisplayName("Спецсимволы VIN")
    public void invalidVinTest() {
        runEvaluation("@@@###", mileage(), city(), false);
    }

    @Test
    @Order(8)
    @DisplayName("Пустой пробег")
    public void emptyMileageTest() {
        runEvaluation(randomVin(), "", city(), false);
    }

    @Test
    @Order(9)
    @DisplayName("Неверный город")
    public void invalidCityTest() {
        runEvaluation(randomVin(), mileage(), "Луноград", false);
    }

    private void runEvaluation(
            String vin,
            String mileage,
            String city,
            boolean success
    ) {
        CompletableFuture<Void> chrome = CompletableFuture.runAsync(() ->
                runWithRetry(
                        chromeHub,
                        chromeEval,
                        chromeLauncher,
                        "Chrome",
                        vin,
                        mileage,
                        city,
                        success
                )
        );

        CompletableFuture<Void> firefox = CompletableFuture.runAsync(() ->
                runWithRetry(
                        firefoxHub,
                        firefoxEval,
                        firefoxLauncher,
                        "Firefox",
                        vin,
                        mileage,
                        city,
                        success
                )
        );

        try {
            CompletableFuture.allOf(chrome, firefox).join();
        } catch (CompletionException e) {
            throw unwrap(e);
        }
    }

    private void runWithRetry(
            Hub hub,
            Eval eval,
            WebLauncher launcher,
            String browser,
            String vin,
            String mileage,
            String city,
            boolean success
    ) {
        RuntimeException last = null;

        for (int attempt = 1; attempt <= 2; attempt++) {
            try {
                runInBrowser(hub, eval, launcher, browser, vin, mileage, city, success);
                return;
            } catch (StaleElementReferenceException | TimeoutException e) {
                last = e;
                reset(launcher);
            } catch (WebDriverException e) {
                last = e;

                if (!browser.equals("Firefox")) {
                    throw e;
                }

                reset(launcher);
            }
        }

        throw last;
    }

    private void runInBrowser(
            Hub hub,
            Eval eval,
            WebLauncher launcher,
            String browser,
            String vin,
            String mileage,
            String city,
            boolean success
    ) {
        hub.openPage(START_URL);
        hub.openEvaluationPage();

        eval.fillEvaluationForm(vin, mileage, city);

        if (success) {
            eval.waitForEvaluationResultPage();
            assertResultPage(launcher.getDriver().getCurrentUrl(), browser);
        } else {
            assertEvalPage(launcher.getDriver().getCurrentUrl(), browser);
        }
    }

    private void assertResultPage(String url, String browser) {
        assertTrue(
                url.contains(RESULT_URL),
                browser + ": нет результата"
        );
    }

    private void assertEvalPage(String url, String browser) {
        assertTrue(
                url.contains(EVAL_URL),
                browser + ": ушёл со страницы"
        );

        assertFalse(
                url.contains(RESULT_URL),
                browser + ": открыл результат"
        );
    }

    private RuntimeException unwrap(CompletionException e) {
        Throwable cause = e.getCause();

        if (cause instanceof RuntimeException runtimeException) {
            return runtimeException;
        }

        return e;
    }

    private void reset(WebLauncher launcher) {
        try {
            launcher.getDriver().navigate().to("about:blank");
        } catch (WebDriverException ignored) {
        }
    }

    private static String randomVin() {
        return VINS.get(RANDOM.nextInt(VINS.size()));
    }

    private static String mileage() {
        return String.valueOf(10000 + RANDOM.nextInt(190000));
    }

    private static String city() {
        return CITIES.get(RANDOM.nextInt(CITIES.size()));
    }

    @AfterAll
    public static void tearDown() {
        try {
            chromeLauncher.closeBrowserCompletely();
        } catch (Exception ignored) {
        }

        try {
            firefoxLauncher.closeBrowserCompletely();
        } catch (Exception ignored) {
        }
    }
}
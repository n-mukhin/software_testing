package guest;

import finder.Eval;
import finder.Hub;
import org.junit.jupiter.api.*;
import starter.WebLauncher;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class B_EvalTest {

    private static final String START_URL = "https://auto.ru/";
    private static final String EVAL_URL = "/evaluation/cars/";
    private static final String RESULT_URL = "evaluation_id=";

    private static final WebLauncher launcher = new WebLauncher();

    private static Hub hub;
    private static Eval eval;

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
        launcher.setupChromeDriver();

        assertNotNull(launcher.getDriver());

        hub = new Hub(launcher.getDriver());
        eval = new Eval(launcher.getDriver());
    }

    @Test
    @Order(1)
    @DisplayName("Оценка 1")
    public void evaluation1Test() {
        runSuccess(VINS.get(0));
    }

    @Test
    @Order(2)
    @DisplayName("Оценка 2")
    public void evaluation2Test() {
        runSuccess(VINS.get(1));
    }

    @Test
    @Order(3)
    @DisplayName("Оценка 3")
    public void evaluation3Test() {
        runSuccess(VINS.get(2));
    }

    @Test
    @Order(4)
    @DisplayName("Оценка 4")
    public void evaluation4Test() {
        runSuccess(VINS.get(3));
    }

    @Test
    @Order(5)
    @DisplayName("Оценка 5")
    public void evaluation5Test() {
        runSuccess(VINS.get(4));
    }

    @Test
    @Order(6)
    @DisplayName("Пустой VIN")
    public void emptyVinTest() {
        runFail("", mileage(), city());
    }

    @Test
    @Order(7)
    @DisplayName("Неверный VIN")
    public void invalidVinTest() {
        runFail("@@@###", mileage(), city());
    }

    @Test
    @Order(8)
    @DisplayName("Пустой пробег")
    public void emptyMileageTest() {
        runFail(randomVin(), "", city());
    }

    @Test
    @Order(9)
    @DisplayName("Неверный город")
    public void invalidCityTest() {
        runFail(randomVin(), mileage(), "Луноград");
    }

    private void runSuccess(String vin) {
        openEvaluation();

        eval.fillEvaluationForm(
                vin,
                mileage(),
                city()
        );

        eval.waitForEvaluationResultPage();

        assertResultPage();
    }

    private void runFail(String vin, String mileage, String city) {
        openEvaluation();

        eval.fillEvaluationForm(vin, mileage, city);

        assertEvaluationPage();
    }

    private void openEvaluation() {
        hub.openPage(START_URL);
        hub.openEvaluationPage();
    }

    private void assertResultPage() {
        String url = launcher.getDriver().getCurrentUrl();

        assertTrue(
                url.contains(RESULT_URL),
                "Нет результата"
        );
    }

    private void assertEvaluationPage() {
        String url = launcher.getDriver().getCurrentUrl();

        assertTrue(
                url.contains(EVAL_URL),
                "Нет страницы оценки"
        );

        assertFalse(
                url.contains(RESULT_URL),
                "Открыт результат"
        );
    }

    private static String randomVin() {
        return VINS.get(RANDOM.nextInt(VINS.size()));
    }

    private static String mileage() {
        return String.valueOf(
                10000 + RANDOM.nextInt(190000)
        );
    }

    private static String city() {
        return CITIES.get(
                RANDOM.nextInt(CITIES.size())
        );
    }
}
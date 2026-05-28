package guest;

import finder.Hub;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import starter.WebLauncher;

import static org.junit.jupiter.api.Assertions.*;

public class A_HeaderTest {

    private static final String START_URL = "https://auto.ru/";

    private static final WebLauncher launcher = new WebLauncher();
    private static Hub hub;

    @BeforeAll
    public static void setUp() {
        launcher.setupChromeDriver();

        assertNotNull(launcher.getDriver());

        hub = new Hub(launcher.getDriver());
    }

    @Test
    @DisplayName("Переход в Легковые")
    public void carsHeaderTest() {
        hub.openPage(START_URL);
        hub.clickCarsButton();
        checkPage("/cars/all/");
    }

    @Test
    @DisplayName("Переход в Мото")
    public void motoHeaderTest() {
        hub.openPage(START_URL);
        hub.clickMotoButton();
        checkPage("/motorcycle/all/");
    }

    @Test
    @DisplayName("Переход в Отчёты")
    public void reportsHeaderTest() {
        hub.openPage(START_URL);
        hub.clickReportsButton();
        checkPage("/history/");
    }

    @Test
    @DisplayName("Переход в Для бизнеса")
    public void businessHeaderTest() {
        hub.openPage(START_URL);
        hub.clickBusinessButton();
        checkPage("/dealer/resellers/");
    }

    @Test
    @DisplayName("Переход в Объявления")
    public void adsHeaderTest() {
        hub.openPage(START_URL);
        hub.clickAdsButton();
        checkPage("/cars/all/");
    }

    @Test
    @DisplayName("Переход в Каталог")
    public void catalogHeaderTest() {
        hub.openPage(START_URL);
        hub.clickCatalogButton();
        checkPage("/catalog/cars/");
    }

    @Test
    @DisplayName("Переход в Дилеры")
    public void dealersHeaderTest() {
        hub.openPage(START_URL);
        hub.clickDealersButton();
        checkPage("/dilery/cars/all/");
    }


    private void checkPage(String expectedUrlPart) {
        long endTime = System.currentTimeMillis() + 10_000;

        while (System.currentTimeMillis() < endTime) {
            String currentUrl = launcher.getDriver().getCurrentUrl();

            boolean correctUrl = currentUrl.contains(expectedUrlPart);
            boolean bodyExists = !launcher.getDriver()
                    .findElements(By.tagName("body"))
                    .isEmpty();

            if (correctUrl && bodyExists) {
                return;
            }

            Thread.onSpinWait();
        }

        throw new TimeoutException(
                ": Ожидалась  URL: " +
                        expectedUrlPart +
                        ", текущий URL: " +
                        launcher.getDriver().getCurrentUrl()
        );
    }
}
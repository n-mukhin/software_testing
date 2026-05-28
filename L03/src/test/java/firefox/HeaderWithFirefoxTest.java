package firefox;

import finder.Hub;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import starter.WebLauncher;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;

public class HeaderWithFirefoxTest {

    private static final String START_URL = "https://auto.ru/";

    private static final WebLauncher chromeLauncher = new WebLauncher();
    private static final WebLauncher firefoxLauncher = new WebLauncher();

    private static Hub chromeHub;
    private static Hub firefoxHub;

    @BeforeAll
    public static void setUp() {
        chromeLauncher.setupChromeDriver();
        firefoxLauncher.setupFirefoxDriver();

        assertNotNull(chromeLauncher.getDriver());
        assertNotNull(firefoxLauncher.getDriver());

        chromeHub = new Hub(chromeLauncher.getDriver());
        firefoxHub = new Hub(firefoxLauncher.getDriver());
    }

    @Test
    @DisplayName("Легковые")
    public void carsHeaderShouldWorkInChromeAndFirefoxAtSameTimeTest() {
        runInChromeAndFirefox(
                () -> {
                    chromeHub.openPage(START_URL);
                    chromeHub.clickCarsButton();
                    checkChromePage("/cars/all/");
                },
                () -> {
                    firefoxHub.openPage(START_URL);
                    firefoxHub.clickCarsButton();
                    checkFirefoxPage("/cars/all/");
                }
        );
    }

    @Test
    @DisplayName("Отчёты")
    public void reportsHeaderShouldWorkInChromeAndFirefoxAtSameTimeTest() {
        runInChromeAndFirefox(
                () -> {
                    chromeHub.openPage(START_URL);
                    chromeHub.clickReportsButton();
                    checkChromePage("/history/");
                },
                () -> {
                    firefoxHub.openPage(START_URL);
                    firefoxHub.clickReportsButton();
                    checkFirefoxPage("/history/");
                }
        );
    }

    @Test
    @DisplayName("Для бизнеса")
    public void businessHeaderShouldWorkInChromeAndFirefoxAtSameTimeTest() {
        runInChromeAndFirefox(
                () -> {
                    chromeHub.openPage(START_URL);
                    chromeHub.clickBusinessButton();
                    checkChromePage("/dealer/resellers/");
                },
                () -> {
                    firefoxHub.openPage(START_URL);
                    firefoxHub.clickBusinessButton();
                    checkFirefoxPage("/dealer/resellers/");
                }
        );
    }

    @Test
    @DisplayName("Объявления")
    public void adsHeaderShouldWorkInChromeAndFirefoxAtSameTimeTest() {
        runInChromeAndFirefox(
                () -> {
                    chromeHub.openPage(START_URL);
                    chromeHub.clickAdsButton();
                    checkChromePage("/cars/all/");
                },
                () -> {
                    firefoxHub.openPage(START_URL);
                    firefoxHub.clickAdsButton();
                    checkFirefoxPage("/cars/all/");
                }
        );
    }

    @Test
    @DisplayName("Каталог")
    public void catalogHeaderShouldWorkInChromeAndFirefoxAtSameTimeTest() {
        runInChromeAndFirefox(
                () -> {
                    chromeHub.openPage(START_URL);
                    chromeHub.clickCatalogButton();
                    checkChromePage("/catalog/cars/");
                },
                () -> {
                    firefoxHub.openPage(START_URL);
                    firefoxHub.clickCatalogButton();
                    checkFirefoxPage("/catalog/cars/");
                }
        );
    }

    @Test
    @DisplayName("Дилеры")
    public void dealersHeaderShouldWorkInChromeAndFirefoxAtSameTimeTest() {
        runInChromeAndFirefox(
                () -> {
                    chromeHub.openPage(START_URL);
                    chromeHub.clickDealersButton();
                    checkChromePage("/dilery/cars/all/");
                },
                () -> {
                    firefoxHub.openPage(START_URL);
                    firefoxHub.clickDealersButton();
                    checkFirefoxPage("/dilery/cars/all/");
                }
        );
    }

    private void runInChromeAndFirefox(Runnable chromeScenario, Runnable firefoxScenario) {
        CompletableFuture<Void> chromeTest = CompletableFuture.runAsync(chromeScenario);
        CompletableFuture<Void> firefoxTest = CompletableFuture.runAsync(firefoxScenario);

        try {
            CompletableFuture.allOf(chromeTest, firefoxTest).join();
        } catch (CompletionException e) {
            Throwable cause = e.getCause();

            if (cause instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }

            throw e;
        }
    }

    private void checkChromePage(String expectedUrlPart) {
        checkPage(chromeLauncher, expectedUrlPart, "Chrome");
    }

    private void checkFirefoxPage(String expectedUrlPart) {
        checkPage(firefoxLauncher, expectedUrlPart, "Firefox");
    }

    private void checkPage(WebLauncher launcher, String expectedUrlPart, String browser) {
        long endTime = System.currentTimeMillis() + 10_000;

        while (System.currentTimeMillis() < endTime) {
            String currentUrl = launcher.getDriver().getCurrentUrl();

            boolean correctUrl = currentUrl.contains(expectedUrlPart);
            boolean bodyExists = !launcher.getDriver()
                    .findElements(By.tagName("body"))
                    .isEmpty();

            boolean notCaptcha = !currentUrl.contains("showcaptcha");
            boolean notBrokenPage = !currentUrl.startsWith("data:");

            if (correctUrl && bodyExists && notCaptcha && notBrokenPage) {
                return;
            }

            Thread.onSpinWait();
        }

        throw new TimeoutException(
                browser + ": Ожидалась URL: " +
                        expectedUrlPart +
                        ", текущий URL: " +
                        launcher.getDriver().getCurrentUrl()
        );
    }
}
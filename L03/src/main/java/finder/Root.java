package finder;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public abstract class Root {

    protected WebDriver driver;
    protected JavascriptExecutor js;
    protected WebDriverWait wait;

    public Root(WebDriver driver) {
        this.driver = driver;
        this.js = (JavascriptExecutor) driver;
        this.wait = new WebDriverWait(driver, Duration.ofMillis(900), Duration.ofMillis(50));

        driver.manage().timeouts().implicitlyWait(Duration.ZERO);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(2));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(2));
    }

    public WebDriver getDriver() {
        return driver;
    }

    public void openPage(String url) {
        js.executeScript("window.location.href = arguments[0];", url);
        waitUntilPageInteractive();
        scrollToTop();
    }

    protected void waitUntilPageInteractive() {
        try {
            wait.until(webDriver -> {
                Object state = js.executeScript("return document.readyState");
                return "interactive".equals(state) || "complete".equals(state);
            });
        } catch (TimeoutException ignored) {
        }
    }

    protected void scrollToTop() {
        try {
            js.executeScript("window.scrollTo(0, 0);");
        } catch (Exception ignored) {
        }
    }

}
package starter;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;


public class WebLauncher {

    private WebDriver driver;

    public void setupChromeDriver() {
        ChromeOptions options = new ChromeOptions();

        options.setPageLoadStrategy(PageLoadStrategy.NONE);

        options.addArguments("--remote-debugging-port=9222");
        options.addArguments("--user-data-dir=/tmp/selenium-chrome");

        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-popup-blocking");

        driver = new ChromeDriver(options);

        attach(driver);
        setupDriver(driver);
    }

    public void setupFirefoxDriver() {
        FirefoxOptions options = new FirefoxOptions();

        options.setPageLoadStrategy(PageLoadStrategy.NONE);
        options.addArguments("--width=1440");
        options.addArguments("--height=900");

        options.addPreference("browser.shell.checkDefaultBrowser", false);
        options.addPreference("browser.aboutConfig.showWarning", false);
        options.addPreference("media.autoplay.default", 5);

        driver = new FirefoxDriver(options);

        attach(driver);
        setupDriver(driver);
    }

    public void openPage(String url) {
        driver.navigate().to(url);
    }
    private void attach(WebDriver webDriver) {
        driver = webDriver;
    }

    private void setupDriver(WebDriver webDriver) {
        webDriver.manage().window().setSize(new Dimension(1440, 900));
    }

    public WebDriver getDriver() {
        return driver;
    }

    public void quitDriver() {
        if (driver != null) {
            driver.quit();
        }

        driver = null;
    }

    public void closeBrowserCompletely() {
        quitDriver();
    }
}
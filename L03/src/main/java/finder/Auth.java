package finder;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.io.Console;
import java.time.Duration;
import java.util.Scanner;

public class Auth extends Root {

    private final By loginButton = By.xpath(
            "//a[contains(@href,'auth.auto.ru/login')]" +
                    "|//button[normalize-space()='Войти' or .//*[normalize-space()='Войти']]"
    );

    private final By autoRuProviderButton = By.xpath(
            "//a[@id='autoru' and contains(@href,'auth.auto.ru')]" +
                    "|//span[@id='autoru']/ancestor::a[contains(@href,'auth.auto.ru')][1]" +
                    "|//*[contains(@class,'AuthFormSocialLogin__provider')][.//*[normalize-space()='Авто.ру'] or contains(normalize-space(.),'Авто.ру')]" +
                    "//*[self::a or self::span or self::button][@id='autoru' or contains(normalize-space(.),'Авто.ру')]"
    );

    private final By loginInput = By.xpath(
            "//input[@name='login' " +
                    "or contains(@placeholder,'Телефон') " +
                    "or contains(@placeholder,'почта') " +
                    "or contains(@placeholder,'Почта')]"
    );

    private final By nextButton = By.xpath(
            "//button[@type='submit' and (contains(normalize-space(.),'Далее') or contains(normalize-space(.),'Войти'))]" +
                    "|//button[contains(normalize-space(.),'Далее')]"
    );

    private final By codeInput = By.xpath(
            "//input[@name='code' " +
                    "or contains(@placeholder,'Код') " +
                    "or contains(@placeholder,'код') " +
                    "or @inputmode='numeric' " +
                    "or @autocomplete='one-time-code']"
    );

    public Auth(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);

        try {
            driver.manage().timeouts().implicitlyWait(Duration.ZERO);
        } catch (Exception ignored) {
        }
    }

    public void loginWithAutoRuMailCode(String email) {
        waitForMailCodeAfterEmail(email);

        WebElement codeField = waitVisible(codeInput);
        String code = readCodeFromTerminal();

        setInputValue(codeField, code);
        codeField.sendKeys(Keys.ENTER);
    }

    public void enterAutoRuEmail(String email) {
        validateEmail(email);

        openAutoRuAuthPage();

        if (firstVisible(loginInput) == null) {
            chooseAutoRuProvider();
        }

        WebElement emailInput = waitVisible(loginInput);
        setInputValue(emailInput, email);
    }

    public void waitForMailCodeAfterEmail(String email) {
        enterAutoRuEmail(email);

        click(waitClickable(nextButton));

        waitVisible(codeInput);
    }

    public void clickLoginButton() {
        openAutoRuAuthPage();
    }

    private void openAutoRuAuthPage() {
        if (isAutoRuAuthPage()) {
            return;
        }

        WebElement button = waitVisible(loginButton);
        click(button);
    }

    private void chooseAutoRuProvider() {
        WebElement autoRu = waitVisible(autoRuProviderButton);

        if (isYandexElement(autoRu)) {
            throw new TimeoutException("Found Yandex provider instead of Auto.ru provider");
        }

        click(autoRu);
    }

    private void validateEmail(String email) {
        if (email == null) {
            throw new IllegalArgumentException("Email не должен быть null");
        }

        String trimmedEmail = email.trim();

        if (trimmedEmail.isEmpty()) {
            throw new IllegalArgumentException("Email не должен быть пустым");
        }

        if (!trimmedEmail.contains("@")) {
            throw new IllegalArgumentException("Email должен содержать @");
        }

        String[] parts = trimmedEmail.split("@", -1);

        if (parts.length != 2 || parts[0].isEmpty() || parts[1].isEmpty()) {
            throw new IllegalArgumentException("Email должен содержать имя и домен");
        }

        if (!parts[1].contains(".")) {
            throw new IllegalArgumentException("Email должен содержать корректный домен");
        }
    }

    private WebElement waitVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    private WebElement waitClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    private String readCodeFromTerminal() {
        String code = javax.swing.JOptionPane.showInputDialog(
                null,
                "Введите код из письма Auto.ru",
                "Код подтверждения",
                javax.swing.JOptionPane.PLAIN_MESSAGE
        );

        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Код не введён");
        }

        return code.trim();
    }

    private WebElement firstVisible(By locator) {
        for (WebElement element : driver.findElements(locator)) {
            try {
                if (element.isDisplayed()) {
                    return element;
                }
            } catch (StaleElementReferenceException ignored) {
            }
        }

        return null;
    }

    private void setInputValue(WebElement input, String value) {
        if (input == null) {
            throw new TimeoutException("Input was not found");
        }

        click(input);

        Keys modifier = System.getProperty("os.name").toLowerCase().contains("mac")
                ? Keys.COMMAND
                : Keys.CONTROL;

        input.sendKeys(modifier + "a");
        input.sendKeys(Keys.BACK_SPACE);
        input.sendKeys(value);
    }

    private void click(WebElement element) {
        if (element == null) {
            throw new TimeoutException("Element for click was not found");
        }

        try {
            new Actions(driver)
                    .moveToElement(element)
                    .click()
                    .perform();
            return;
        } catch (Exception ignored) {
        }

        try {
            element.click();
        } catch (ElementClickInterceptedException | NoSuchElementException | StaleElementReferenceException ignored) {
            click(element);
        }
    }

    private boolean isAutoRuAuthPage() {
        String url = driver.getCurrentUrl();

        return url.contains("auth.auto.ru/login")
                || url.contains("auth.auto.ru");
    }

    private boolean isYandexElement(WebElement element) {
        try {
            String text = element.getText();
            String id = element.getAttribute("id");
            String href = element.getAttribute("href");

            return containsIgnoreCase(text, "яндекс")
                    || containsIgnoreCase(text, "yandex")
                    || containsIgnoreCase(id, "yandex")
                    || containsIgnoreCase(href, "passport.yandex");
        } catch (StaleElementReferenceException ignored) {
            return false;
        }
    }

    private boolean containsIgnoreCase(String value, String part) {
        return value != null && value.toLowerCase().contains(part.toLowerCase());
    }
}
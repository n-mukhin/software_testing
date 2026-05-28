package finder;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class Eval extends Root {

    private final By vinInput = By.xpath(
            "//div[contains(@class,'EvaluationFormStart')]//input[@maxlength='200' and not(@name='mileage')]"
    );

    private final By mileageInput = By.xpath(
            "//input[@name='mileage']"
    );

    private final By cityInput = By.xpath(
            "//div[contains(@class,'GeoInputWithSuggest')]//input"
    );

    private final By citySuggestion = By.xpath(
            "(//*[contains(@class,'RichInput__suggest')]//*[@role='menuitem'])[1]"
    );

    private final By submitButton = By.xpath(
            "//button[contains(@class,'EvaluationFormStart__submitButton')]"
    );

    public Eval(WebDriver driver) {
        super(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(1));
        PageFactory.initElements(driver, this);
    }

    public void fillEvaluationForm(String vin, String mileage, String city) {
        waitForEvaluationPage();

        setInputValue(
                wait.until(ExpectedConditions.visibilityOfElementLocated(vinInput)),
                vin
        );

        setInputValue(
                wait.until(ExpectedConditions.visibilityOfElementLocated(mileageInput)),
                mileage
        );

        setCityValue(
                wait.until(ExpectedConditions.visibilityOfElementLocated(cityInput)),
                city
        );

        selectCity();

        clickSubmitButton();
    }

    public void waitForEvaluationPage() {
        wait.until(ExpectedConditions.urlContains("/evaluation/cars/"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(vinInput));
        wait.until(ExpectedConditions.visibilityOfElementLocated(mileageInput));
        wait.until(ExpectedConditions.visibilityOfElementLocated(cityInput));
    }

    public void waitForEvaluationResultPage() {
        wait.until(d ->
                d.getCurrentUrl().contains("/evaluation/cars/?evaluation_id=")
        );
    }

    private void selectCity() {
        try {
            wait.until(
                    ExpectedConditions.visibilityOfElementLocated(citySuggestion)
            ).click();
        } catch (TimeoutException ignored) {
        }
    }

    private void clickSubmitButton() {
        click(
                wait.until(
                        ExpectedConditions.elementToBeClickable(submitButton)
                )
        );
    }

    private void setCityValue(WebElement input, String value) {
        if (input == null) {
            throw new TimeoutException("Поле не найдено");
        }

        Keys modifier = getModifier();

        new Actions(driver)
                .click(input)
                .keyDown(modifier)
                .sendKeys("a")
                .keyUp(modifier)
                .sendKeys(Keys.DELETE)
                .sendKeys(value)
                .perform();
    }

    private void setInputValue(WebElement input, String value) {
        if (input == null) {
            throw new TimeoutException("Поле не найдено");
        }

        click(input);

        input.sendKeys(Keys.chord(getModifier(), "a"));
        input.sendKeys(Keys.BACK_SPACE);
        input.sendKeys(value);
    }

    private Keys getModifier() {
        return System.getProperty("os.name").toLowerCase().contains("mac")
                ? Keys.COMMAND
                : Keys.CONTROL;
    }

    private void click(WebElement element) {
        if (element == null) {
            throw new TimeoutException("Элемент не найден");
        }

        new Actions(driver)
                .moveToElement(element)
                .click()
                .perform();
    }
}
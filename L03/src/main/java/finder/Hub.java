package finder;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class Hub extends Root {

    private final By evaluationButton = By.xpath("//li[@data-id='cars-evaluation']//a[contains(@href,'/evaluation/cars/')]");
    private final By carsButton = By.xpath("//li[@data-id='cars']//a");
    private final By motoButton = By.xpath("//li[@data-id='moto']//a");
    private final By reportsButton = By.xpath("//li[@data-id='history']//a");
    private final By businessButton = By.xpath("//li[@data-id='dealers']//a[contains(@href,'/dealer/resellers/')]");
    private final By adsButton = By.xpath("//a[contains(@class,'ServiceNavigation__link_name_listing')]");
    private final By catalogButton = By.xpath("//a[contains(@class,'ServiceNavigation__link_name_catalog')]");
    private final By dealersButton = By.xpath("//a[contains(@class,'ServiceNavigation__link_name_dealers')]");


    private final By favoritesButton = By.xpath(
            "//*[contains(@class,'HeaderMyLink_type_favorites')]"
    );

    private final By favoritesPopup = By.xpath(
            "//*[contains(@class,'HeaderFavoritesPopup')]"
    );

    private final By addFavoriteButtons = By.xpath(
            "//*[contains(@class,'HeaderFavoritesPopup')]" +
                    "//*[local-name()='svg' and contains(@class,'SvgFavoriteOutline')]" +
                    "/ancestor::*[contains(@class,'ButtonFavorite')][1]"
    );

    private final By removeFavoriteButtons = By.xpath(
            "//*[contains(@class,'HeaderFavoritesPopup')]" +
                    "//*[local-name()='svg' and contains(@class,'SvgFavoriteFilled')]" +
                    "/ancestor::*[contains(@class,'ButtonFavorite')][1]"
    );

    private final By closePopupButton = By.xpath(
            "//*[@data-testid='modalCloser' and @role='button']"
    );

    public Hub(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    @Override
    public void openPage(String url) {
        try {
            driver.get(url);
        } catch (TimeoutException ignored) {
            waitUntilPageInteractive();
        }
        closeMarketingPopupIfVisible();
    }

    public WebDriver getDriver() {
        return driver;
    }

    public void clickFavoritesButton() {
        WebElement button = wait(1)
                .until(ExpectedConditions.elementToBeClickable(favoritesButton));

        button.click();
    }

    public boolean isFavoritesPopupOpened() {
        return isPresent(
                By.xpath(
                        "//*[contains(@class,'HeaderFavoritesPopup')]" +
                                "//*[contains(text(),'Избранное')]"
                ),
                3
        );
    }

    public boolean areFavoriteOffersLoaded() {
        try {
            return wait(3).until(d ->
                    !d.findElements(addFavoriteButtons).isEmpty()
                            || !d.findElements(removeFavoriteButtons).isEmpty()
            );
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean addOfferToFavorites(int index) {
        int before = getAddedOffersCount();

        clickFavoriteButton(addFavoriteButtons, index);

        return wait(1).until(
                d -> getAddedOffersCount() > before
        );
    }

    public boolean removeOfferFromFavorites(int index) {
        int before = getAddedOffersCount();

        clickFavoriteButton(removeFavoriteButtons, index);

        return wait(1).until(
                d -> getAddedOffersCount() < before
        );
    }

    public boolean hasRemoveFromFavoriteButton() {
        return isPresent(removeFavoriteButtons, 5);
    }

    public int getAddedOffersCount() {
        return driver.findElements(removeFavoriteButtons).size();
    }

    private void clickFavoriteButton(By locator, int index) {
        wait(1).until(ExpectedConditions.presenceOfElementLocated(favoritesPopup));

        List<WebElement> buttons = driver.findElements(locator);

        if (buttons.isEmpty()) {
            throw new TimeoutException("Кнопки не найдены");
        }

        if (index < 0 || index >= buttons.size()) {
            throw new IndexOutOfBoundsException("Неверный индекс: " + index);
        }

        WebElement button = buttons.get(index);

        wait(3).until(ExpectedConditions.visibilityOf(button));

        new Actions(driver)
                .moveToElement(button)
                .click()
                .perform();
    }

    public int getRemoveFromFavoriteButtonsCount() {
        return driver.findElements(removeFavoriteButtons).size();
    }

    public void openEvaluationPage() {
        closeMarketingPopupIfVisible();

        try {
            click(waitClickable(evaluationButton));
        } catch (TimeoutException e) {
            driver.get("https://auto.ru/evaluation/cars/");
        }
    }

    public void clickCarsButton() {
        clickFresh(carsButton);
    }

    public void clickMotoButton() {
        clickFresh(motoButton);
    }

    public void clickReportsButton() {
        clickFresh(reportsButton);
    }

    public void clickBusinessButton() {
        clickFresh(businessButton);
    }

    public void clickAdsButton() {
        clickFresh(adsButton);
    }

    public void clickCatalogButton() {
        clickFresh(catalogButton);
    }

    public void clickDealersButton() {
        clickFresh(dealersButton);
    }


    public void closeMarketingPopupIfVisible() {
        WebElement close = firstVisible(closePopupButton);

        if (close != null) {
            click(close);
        }
    }

    private WebDriverWait wait(int seconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(seconds));
    }

    private boolean isPresent(By locator, int seconds) {
        try {
            return wait(seconds).until(
                    d -> !d.findElements(locator).isEmpty()
            );
        } catch (TimeoutException e) {
            return false;
        }
    }

    private void clickFresh(By locator) {
        try {
            click(waitClickable(locator));
        } catch (StaleElementReferenceException e) {
            click(waitClickable(locator));
        }
    }

    private WebElement waitClickable(By locator) {
        return wait(1).until(ExpectedConditions.elementToBeClickable(locator));
    }

    private WebElement firstVisible(By locator) {
        try {
            return wait(1).until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            return null;
        }
    }

    private void click(WebElement element) {
        if (element == null) {
            throw new TimeoutException("Элемент не найден");
        }

        element.click();
    }

}
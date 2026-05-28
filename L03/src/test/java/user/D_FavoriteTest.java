package user;

import finder.Hub;
import org.junit.jupiter.api.*;
import starter.WebLauncher;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class D_FavoriteTest {

    private static final String START_URL = "https://auto.ru/";
    private static final int OFFERS_COUNT = 5;

    private static final WebLauncher launcher = new WebLauncher();
    private static Hub hub;

    @BeforeAll
    public static void setUp() {
        launcher.setupChromeDriver();

        assertNotNull(launcher.getDriver());

        hub = new Hub(launcher.getDriver());
    }

    @Test
    @Order(1)
    @DisplayName("Открытие избранного")
    public void favoritesPopupShouldBeOpenedTest() {
        openFavoritesPopup();

        assertTrue(hub.isFavoritesPopupOpened(), "Popup не открылся");
    }

    @Test
    @Order(2)
    @DisplayName("Добавление")
    public void oneOfferShouldBeAddedToFavoritesTest() {
        openFavoritesPopup();

        assertTrue(
                hub.addOfferToFavorites(0),
                "Не добавилось"
        );
    }

    @Test
    @Order(3)
    @DisplayName("Массовое добавление")
    public void severalOffersShouldBeAddedToFavoritesTest() {
        openFavoritesPopup();

        for (int i = 0; i < OFFERS_COUNT; i++) {
            assertTrue(
                    hub.addOfferToFavorites(0),
                    "Не добавилось: " + (i + 1)
            );
        }
    }

    @Test
    @Order(4)
    @DisplayName("Массовое удаление")
    public void severalOffersShouldBeRemovedFromFavoritesTest() {
        openFavoritesPopup();

        int count = hub.getRemoveFromFavoriteButtonsCount();

        if (count <= 2) {
            return;
        }

        for (int i = 0; i < count - 2; i++) {
            assertTrue(
                    hub.removeOfferFromFavorites(0),
                    "Не удалилось: " + (i + 1)
            );
        }

        assertEquals(
                2,
                hub.getRemoveFromFavoriteButtonsCount(),
                "Должно остаться 2"
        );
    }

    @Test
    @Order(5)
    @DisplayName("Отрицательный индекс")
    public void offerShouldNotBeAddedWithNegativeIndexTest() {
        openFavoritesPopup();

        assertThrows(
                IndexOutOfBoundsException.class,
                () -> hub.addOfferToFavorites(-1)
        );
    }

    @Test
    @Order(6)
    @DisplayName("Неверный индекс добавления")
    public void offerShouldNotBeAddedWithWrongIndexTest() {
        openFavoritesPopup();

        assertThrows(
                IndexOutOfBoundsException.class,
                () -> hub.addOfferToFavorites(999)
        );
    }

    @Test
    @Order(7)
    @DisplayName("Неверный индекс удаления")
    public void offerShouldNotBeRemovedWithWrongIndexTest() {
        openFavoritesPopup();

        if (!hub.hasRemoveFromFavoriteButton()) {
            hub.addOfferToFavorites(0);
        }

        assertThrows(
                IndexOutOfBoundsException.class,
                () -> hub.removeOfferFromFavorites(999)
        );
    }

    private void openFavoritesPopup() {
        hub.openPage(START_URL);

        hub.clickFavoritesButton();

        assertTrue(
                hub.isFavoritesPopupOpened(),
                "Popup не открылся"
        );

        assertTrue(
                hub.areFavoriteOffersLoaded(),
                "Нет объявлений"
        );
    }
}
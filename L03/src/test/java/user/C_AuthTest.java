package user;

import finder.Auth;
import finder.Hub;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import starter.WebLauncher;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class C_AuthTest {

    private static final String START_URL = "https://auto.ru/";
    private static final String EMAIL = "placeholder";

    private static final WebLauncher launcher = new WebLauncher();

    private static Hub hub;
    private static Auth auth;

    @BeforeAll
    public static void setUp() {
        launcher.setupChromeDriver();

        assertNotNull(launcher.getDriver());

        hub = new Hub(launcher.getDriver());
        auth = new Auth(launcher.getDriver());
    }

    @Test
    @Order(1)
    @DisplayName("1. Негативный сценарий: пустой email")
    public void emptyEmailTest() {
        hub.openPage(START_URL);

        assertThrows(
                IllegalArgumentException.class,
                () -> auth.enterAutoRuEmail("")
        );
    }

    @Test
    @Order(2)
    @DisplayName("2. Негативный сценарий: email из пробелов")
    public void blankEmailTest() {
        hub.openPage(START_URL);

        assertThrows(
                IllegalArgumentException.class,
                () -> auth.enterAutoRuEmail("   ")
        );
    }

    @Test
    @Order(3)
    @DisplayName("3. Негативный сценарий: email без @")
    public void emailWithoutAtTest() {
        hub.openPage(START_URL);

        assertThrows(
                IllegalArgumentException.class,
                () -> auth.enterAutoRuEmail("placeholderyandex.ru")
        );
    }

    @Test
    @Order(4)
    @DisplayName("4. Негативный сценарий: email без домена")
    public void emailWithoutDomainTest() {
        hub.openPage(START_URL);

        assertThrows(
                IllegalArgumentException.class,
                () -> auth.enterAutoRuEmail("placeholder@")
        );
    }

    @Test
    @Order(5)
    @DisplayName("5. Негативный сценарий: null email")
    public void nullEmailTest() {
        hub.openPage(START_URL);

        assertThrows(
                IllegalArgumentException.class,
                () -> auth.enterAutoRuEmail(null)
        );
    }

    @Test
    @Order(6)
    @DisplayName("6. Открытие главной страницы")
    public void openMainPageTest() {
        assertDoesNotThrow(() -> hub.openPage(START_URL));
    }

    @Test
    @Order(7)
    @DisplayName("7. Открытие формы авторизации")
    public void openAuthFormTest() {
        hub.openPage(START_URL);

        assertDoesNotThrow(() -> auth.clickLoginButton());
    }

    @Test
    @Order(8)
    @DisplayName("8. Ввод корректного email")
    public void validEmailInputTest() {
        hub.openPage(START_URL);

        assertDoesNotThrow(() -> auth.enterAutoRuEmail(EMAIL));
    }

    @Test
    @Order(9)
    @DisplayName("9. Появление поля для ввода кода из письма")
    public void mailCodeFieldAppearsTest() {
        hub.openPage(START_URL);

        assertDoesNotThrow(() -> auth.waitForMailCodeAfterEmail(EMAIL));
    }

    @Test
    @Order(10)
    @DisplayName("10. Полный сценарий авторизации через email и код")
    public void chromeLoginTest() {
        hub.openPage(START_URL);

        auth.loginWithAutoRuMailCode(EMAIL);
    }
}
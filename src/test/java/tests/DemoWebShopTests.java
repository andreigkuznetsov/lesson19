package tests;

import com.codeborne.selenide.Configuration;
import config.AppConfig;
import io.restassured.RestAssured;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static specs.RestAssuredDwsSpec.requestSpec;


public class DemoWebShopTests extends TestBase {

    public static AppConfig webConfig = ConfigFactory.create(AppConfig.class, System.getProperties());

    String authorizationCookie;

    @Test
    @DisplayName("Изменить Имя и Фамилию пользовтаеля")
    void changeNameLastNameTest() {
        RestAssured.baseURI = webConfig.apiUrl();
        Configuration.baseUrl = webConfig.webUrl();

        step("Получить cookie через api, установить его в браузере", () -> {
            authorizationCookie =
                    given(requestSpec)
                            .contentType("application/x-www-form-urlencoded")
                            .formParam("Email", webConfig.userLogin())
                            .formParam("Password", webConfig.userPassword())
                            .when()
                            .post("login")
                            .then()
                            .log().body()
                            .statusCode(302)
                            .extract()
                            .cookie("NOPCOMMERCE.AUTH");
        });

        step("Открыть минимальный контент, потому что cookie можно установить при открытии сайта", () ->
                open("Themes/DefaultClean/Content/images/logo.png"));

        step("Установить cookie в браузер", () ->
                getWebDriver().manage().addCookie(
                        new Cookie("NOPCOMMERCE.AUTH", authorizationCookie)));

        step("Открыть страницу с данными пользователя", () ->
                open("customer/info"));

        step("Проверить, что Gender соответствует", () ->
                $("#gender-male").shouldHave(attribute("value", "M")));

        step("Проверить, что Имя соответствует", () ->
                $("#FirstName").shouldHave(attribute("value", "Jewel")));

        step("Проверить, что Фамилия соответствует", () ->
                $("#LastName").shouldHave(attribute("value", "Vandervort")));

        step("Проверить, что E-mail соответствует", () ->
                $("#Email").shouldHave(attribute("value", webConfig.userLogin())));

    }

}
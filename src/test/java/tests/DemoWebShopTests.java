package tests;

import com.codeborne.selenide.Configuration;
import com.github.javafaker.Faker;
import config.AppConfig;
import io.restassured.RestAssured;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;

import java.util.Locale;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static specs.RestAssuredSpec.requestSpec;


public class DemoWebShopTests extends TestBase {

    public static AppConfig webConfig = ConfigFactory.create(AppConfig.class, System.getProperties());
    public static String authorizationCookie;

    Faker faker = new Faker(new Locale("en"));

    String firstName = faker.name().firstName();
    String lastName = faker.name().lastName();

    String cookieRequest =
                    "__RequestVerificationToken=lGX50K9LCDhFQxnJ1Av1wWxaEo-y4EVte8NGA6vX0b19Gx1ERN" +
                    "Ff3xCVaOci7KEEICshWfTCjR510EcKzLqExMjP9f7bXuguu24GjtVQ1TI1;" +
                    "NOPCOMMERCE.AUTH=12ECF253B5CADCBF5C313190CD060D3E89750693D1D358B92CF8B08DC862F" +
                    "7AD7E912B219D9EEBD2756C46C804EB8B0AB5E40E09A2DC5AB0140CF7149C36DA5EBAD28B58F186307E3558191" +
                    "9116EF6D50D95C6C6C07F4B2089D61D9E8B8EB6C599E6B243B4DE832556477C63138BADA0BB5F9D799E5F42CE9" +
                    "997B675328124C8FD5D2B6A0096E29D8EB059A6C4935865A57CA140B77E889E18732F83F957C459;";

    String RequestVerificationToken = "gfwDqchwDW-6s4SRjLA8JxHe9etCVoq1I4LXMJx4pJO0aT8fgyrtVxLMKqkiL" +
            "3l116DePf_8k61rcFLJsZiSJHtwaC9iFYy2RigtfHk4LqiFtseZ9Wq6TCPPpua2DhLA0";

    @Test
    @DisplayName("Изменить Имя и Фамилию пользовтаеля")
    void changeNameLastNameTest() {
        RestAssured.baseURI = webConfig.apiUrl();
        Configuration.baseUrl = webConfig.webUrl();
        //SelenideLogger.addListener("AllureSelenide", new AllureSelenide());

        step("Получить cookie через api, установить его в браузере", () -> {
            authorizationCookie =
                    given(requestSpec)
                            .contentType("application/x-www-form-urlencoded")
                            //.formParam("Email", webConfig.userLogin())
                            //.formParam("Password", webConfig.userPassword())
                            .formParam("Email", "andysmith@email.com")
                            .formParam("Password", "654321")
                            .when()
                            .post("login")
                            .then()
                            .log().body()
                            .statusCode(302)
                            .extract()
                            .cookie("NOPCOMMERCE.AUTH");
        });

        step("Открыть минимальный контент, потому что cookie можно установить при открытии сайта", () ->
                open("/Themes/DefaultClean/Content/images/logo.png"));

        step("Установить cookie в браузер", () ->
                getWebDriver().manage().addCookie(
                        new Cookie("NOPCOMMERCE.AUTH", authorizationCookie)));


        step("Изменить Имя и Фамилию пользователя", () -> {
            given(requestSpec)
                            .cookie(cookieRequest)
                            .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                            .formParam("__RequestVerificationToken", RequestVerificationToken)
                            .formParam("Gender", "M")
                            .formParam("FirstName", firstName)
                            .formParam("LastName", lastName)
                            .formParam("Email", webConfig.userLogin())
                            .formParam("save-info-button", "Save")
                            .when()
                            .post("customer/info")
                            .then()
                            .log().body()
                            .statusCode(302);
        });

        step("Открыть страницу с данными пользователя", () ->
                open("customer/info"));

        step("Проверить, что Имя соответствует", () ->
                $("#FirstName").shouldHave(attribute("value", firstName)));

        step("Проверить, что Фамилия соответствует", () ->
                $("#LastName").shouldHave(attribute("value", lastName)));

    }

}
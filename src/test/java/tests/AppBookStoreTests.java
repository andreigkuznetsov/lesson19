package tests;

import config.AppConfig;
import io.restassured.RestAssured;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static filters.CustomLogFilter.customLogFilter;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.is;

public class AppBookStoreTests {

    public static AppConfig webConfig = ConfigFactory.create(AppConfig.class, System.getProperties());

    @BeforeAll
    static void startUrl() {
        RestAssured.baseURI = webConfig.apiAbsUrl();

    }

    @Test
    @DisplayName("Повторное создание уже зарегистрированного пользователя")
    void tryToCreateExistsUserTest() {

        step("Повторное создание уже зарегистрированного пользователя", () -> {
            given()
                    .filter(customLogFilter().withCustomTemplates())
                    .contentType("application/json")
                    .accept("application/json")
                    .body(webConfig.absData().toString())
                    .when()
                    .log().uri()
                    .log().body()
                    .post("Account/v1/User")
                    .then()
                    .log().body()
                    .statusCode(406)
                    .body(matchesJsonSchemaInClasspath("shemas/UnSuccessCreateUserScheme.json"))
                    .body("code", is("1204"))
                    .body("message", is("User exists!"));
        });
    }

    @Test
    @DisplayName("Генерация токена для зарегистрированного пользователя")
    void generateTokenSuccessTest() {

        step("Генерация токена для зарегистрированного пользователя", () -> {
            given()
                    .filter(customLogFilter().withCustomTemplates())
                    .contentType("application/json")
                    .accept("application/json")
                    .body(webConfig.absData().toString())
                    .when()
                    .log().uri()
                    .log().body()
                    .post("Account/v1/GenerateToken")
                    .then()
                    .log().body()
                    .statusCode(200)
                    .body(matchesJsonSchemaInClasspath("shemas/SuccessGenerateTokenScheme.json"))
                    .body("status", is("Success"))
                    .body("result", is("User authorized successfully."));
        });
    }

    @Test
    @DisplayName("Генерация токена для незарегистрированного пользователя")
    void generateTokenUnSuccessTest() {

        step("Генерация токена для незарегистрированного пользователя", () -> {
            given()
                    .filter(customLogFilter().withCustomTemplates())
                    .contentType("application/json")
                    .accept("application/json")
                    .body(webConfig.absNoData().toString())
                    .when()
                    .log().uri()
                    .log().body()
                    .post("Account/v1/GenerateToken")
                    .then()
                    .log().body()
                    .statusCode(200)
                    .body(matchesJsonSchemaInClasspath("shemas/UnSuccessGenerateTokenScheme.json"))
                    .body("status", is("Failed"))
                    .body("result", is("User authorization failed."));
        });
    }

    @Test
    @DisplayName("Запрос данных незарегистрированного пользователя")
    void displayUserUnSuccessTest() {

        step("Запрос данных незарегистрированного пользователя", () -> {
            given()
                    .filter(customLogFilter().withCustomTemplates())
                    .log().uri()
                    .log().body()
                    .get("/Account/v1/User/123")
                    .then()
                    .log().body()
                    .statusCode(401)
                    .body(matchesJsonSchemaInClasspath("shemas/UnSuccessDispDelUserScheme.json"))
                    .body("code", is("1200"))
                    .body("message", is("User not authorized!"));
        });
    }

    @Test
    @DisplayName("Удаление незарегистрированного пользователя")
    void deleteUserUnSuccessTest() {

        step("Удаление незарегистрированного пользователя", () -> {
            given()
                    .filter(customLogFilter().withCustomTemplates())
                    .log().uri()
                    .log().body()
                    .delete("/Account/v1/User/123")
                    .then()
                    .log().body()
                    .statusCode(401)
                    .body(matchesJsonSchemaInClasspath("shemas/UnSuccessDispDelUserScheme.json"))
                    .body("code", is("1200"))
                    .body("message", is("User not authorized!"));
        });
    }

}

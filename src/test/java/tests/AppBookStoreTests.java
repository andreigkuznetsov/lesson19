package tests;

import base.ApiEndpointsAbs;
import config.AppConfig;
import io.restassured.RestAssured;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static specs.RestAssuredAbsSpec.requestSpec;


public class AppBookStoreTests {

    public static AppConfig webConfig = ConfigFactory.create(AppConfig.class, System.getProperties());

    @BeforeAll
    static void startAbsUrl() {
        RestAssured.baseURI = webConfig.apiAbsUrl();

    }

    @Test
    @DisplayName("Повторное создание уже зарегистрированного пользователя")
    void tryToCreateExistsUserTest() {

        step("Повторное создание уже зарегистрированного пользователя", () -> {
            given(requestSpec)
                    .contentType(JSON)
                    .body(webConfig.absData())
                    .when()
                    .post(ApiEndpointsAbs.user)
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
            given(requestSpec)
                    .contentType(JSON)
                    .body(webConfig.absData())
                    .when()
                    .post(ApiEndpointsAbs.gentoken)
                    .then()
                    .log().body()
                    .statusCode(200)
                    .body(matchesJsonSchemaInClasspath("shemas/SuccessGenerateTokenScheme.json"))
                    .body("token", is(notNullValue()))
                    .body("expires", is(notNullValue()))
                    .body("status", is("Success"))
                    .body("result", is("User authorized successfully."));
        });
    }

    @Test
    @DisplayName("Генерация токена для незарегистрированного пользователя")
    void generateTokenUnSuccessTest() {

        step("Генерация токена для незарегистрированного пользователя", () -> {
            given(requestSpec)
                    .contentType(JSON)
                    .body(webConfig.absNoData())
                    .when()
                    .post(ApiEndpointsAbs.gentoken)
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
            given(requestSpec)
                    .get(ApiEndpointsAbs.user + "/123")
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
            given(requestSpec)
                    .delete(ApiEndpointsAbs.user + "/123")
                    .then()
                    .log().body()
                    .statusCode(401)
                    .body(matchesJsonSchemaInClasspath("shemas/UnSuccessDispDelUserScheme.json"))
                    .body("code", is("1200"))
                    .body("message", is("User not authorized!"));
        });
    }

}

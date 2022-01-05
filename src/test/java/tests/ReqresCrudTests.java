package tests;

import config.AppConfig;
import io.restassured.RestAssured;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.*;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.core.Is.is;
import static specs.RestAssuredSpec.requestSpec;


public class ReqresCrudTests {

    public static AppConfig webConfig = ConfigFactory.create(AppConfig.class, System.getProperties());

    @BeforeEach
    void startUrl() {
        RestAssured.baseURI = webConfig.apiReqUrl();

    }

    @Test
    @DisplayName("Создать пользователя с именем и должностью, изменить должность")
    void createTest() {

        step("Создать пользователя с именем и должностью", () -> {
            given(requestSpec)
                    .contentType(JSON)
                    .body(webConfig.requestBody().toString())
                    .when()
                    .post("api/users")
                    .then()
                    .log().body()
                    .statusCode(201)
                    .body("name", is(webConfig.name()),
                            "job", is(webConfig.job()));
        });

        step("Изменить должность пользователя", () -> {
            given(requestSpec)
                    .contentType(JSON)
                    .body(webConfig.newrequestBody().toString())
                    .when()
                    .put("api/users/2")
                    .then()
                    .log().body()
                    .statusCode(200)
                    .body("name", is(webConfig.name()),
                            "job", is(webConfig.newJob()));
        });
    }

    @Test
    @DisplayName("Удалить данные пользователя")
    void deleteTest() {

        step("Удалить данные пользователя", () -> {
            given(requestSpec)
                    .delete("/api/users/2")
                    .then()
                    .log().body()
                    .statusCode(204);
        });
    }

    @Test
    @DisplayName("Создать пользователя с email и паролем")
    void registerTest() {

        step("Создать пользователя с email и паролем", () -> {
            given(requestSpec)
                    .contentType(JSON)
                    .body(webConfig.anotherrequestBody().toString())
                    .when()
                    .post("api/register")
                    .then()
                    .log().body()
                    .statusCode(200)
                    .body("id", is(4),
                            "token", is(webConfig.ehToken()));
        });
    }

    @Test
    @DisplayName("Получить данные пользователя")
    void getRegisteredUserTest() {

        step("Получить данные пользователя", () -> {
            given(requestSpec)
                    .get("api/users/4")
                    .then()
                    .log().body()
                    .statusCode(200)
                    .body("data.email", is(webConfig.ehEmail()),
                            "data.id", is(4),
                            "data.first_name", is("Eve"),
                            "data.last_name", is("Holt"));
        });
    }

    @Test
    @DisplayName("Авторизироваться с email и паролем пользователя")
    void loginTest() {

        step("Авторизироваться с email и паролем пользовтеля", () -> {
            given(requestSpec)
                    .contentType(JSON)
                    .body(webConfig.anotherrequestBody().toString())
                    .when()
                    .post("api/login")
                    .then()
                    .log().body()
                    .statusCode(200)
                    .body("token", is(webConfig.ehToken()));
        });
    }
}
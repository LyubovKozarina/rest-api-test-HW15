package tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsEqual.equalTo;

public class ReqresApiTests {

    String apiKey = "reqres-free-v1";

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "https://reqres.in";
        RestAssured.basePath = "/api";
    }

    @Test
    @DisplayName("Получение одиночного пользователя по id")
    void getSingleUserTest() {
        given()
                .header("x-api-key", apiKey)
                .when()
                .get("/users/2")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body("data.id", equalTo(2))
                .body("data.email", containsString("@reqres.in"));
    }

    @Test
    @DisplayName("Ошибка при запросе несуществующего пользователя - проверка 404 статус кода")
    void getNonExistentUserTest() {
        given()
                .header("x-api-key", apiKey)
                .when()
                .get("/users/23")
                .then()
                .log().status()
                .log().body()
                .statusCode(404);
    }

    @Test
    @DisplayName("Проверка email 'michael.lawson@reqres.in' в списке пользователей")
    void verifyEmailInUserListTest() {
        given()
                .header("x-api-key", apiKey)
                .queryParam("page", 2)
                .log().uri()
                .when()
                .get("/users")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body("data.email", hasItem("michael.lawson@reqres.in"));
    }

    @Test
    @DisplayName("Создание пользователя")
    void createUserTest() {
        String body = """
                {
                    "name": "morpheus",
                    "job": "leader"
                }
                """;

        given()
                .header("x-api-key", apiKey)
                .contentType(ContentType.JSON)
                .body(body)
                .log().uri()
                .when()
                .post("/users")
                .then()
                .log().status()
                .log().body()
                .statusCode(201)
                .body("name", equalTo("morpheus"))
                .body("job", equalTo("leader"))
                .body("id", notNullValue());
    }

    @Test
    @DisplayName("Изменение информации о пользователе")
    void updateUserTest() {
        String body = """
                {
                    "name": "morpheus",
                    "job": "zion resident"
                }
                """;

        given()
                .header("x-api-key", apiKey)
                .contentType(ContentType.JSON)
                .body(body)
                .log().uri()
                .when()
                .put("/users/2")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body("name", equalTo("morpheus"))
                .body("job", equalTo("zion resident"));
    }
}

package tests;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsEqual.equalTo;
import static io.restassured.RestAssured.get;

public class ReqresApiTests {

    String baseUrl = "https://reqres.in/api";
    String apiKey = "reqres-free-v1";


    @Test
    @DisplayName("Получение одиночного пользователя по id")
    void getSingleUserTest() {
        get(baseUrl + "/users/2")
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
                .get(baseUrl + "/users/23")
                .then()
                .log().status()
                .log().body()
                .statusCode(404);
    }

    @Test
    @DisplayName("Проверка email 'michael.lawson@reqres.in' в списке пользователей")
    void verifyEmailInUserListTest() {
        given()
                .queryParam("page", 2)
                .log().uri()
                .when()
                .get(baseUrl + "/users")
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
                .contentType(ContentType.JSON)
                .header("x-api-key", apiKey)
                .body(body)
                .log().uri()
                .when()
                .post(baseUrl + "/users")
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
                .contentType(ContentType.JSON)
                .header("x-api-key", apiKey)
                .body(body)
                .log().uri()
                .when()
                .put(baseUrl + "/users/2")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body("name", equalTo("morpheus"))
                .body("job", equalTo("zion resident"));
    }

}


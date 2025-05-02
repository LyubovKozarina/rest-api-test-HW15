package tests;

import io.restassured.RestAssured;
import models.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;
import static specs.CreateUserSpec.createUserRequestSpec;
import static specs.CreateUserSpec.createUserResponseSpec;
import static specs.GetUserListSpec.getUserListRequestSpec;
import static specs.GetUserListSpec.getUserListResponseSpec;
import static specs.GetUserSpec.*;
import static specs.UpdateUserSpec.updateUserRequestSpec;
import static specs.UpdateUserSpec.updateUserResponseSpec;

public class ReqresApiTests {

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "https://reqres.in";
        RestAssured.basePath = "/api";
    }

    @Test
    @DisplayName("Получение одиночного пользователя по id")
    void getSingleUserTest() {
        getSingleUserTest(2);
    }

    void getSingleUserTest(int userId) {
        SingleUserResponseModel response = step("Получение пользователя по id =" + userId, () ->
                given(getSingleUserRequestSpec)
                        .when()
                        .get("/users/" + userId)
                        .then()
                        .spec(getSingleUserResponseSpec)
                        .extract().as(SingleUserResponseModel.class));

        step("Проверка данных пользователя", () -> {
            assertEquals(userId, response.getData().getId());
            assertTrue(response.getData().getEmail().contains("@reqres.in"));
        });
    }

    @Test
    @DisplayName("Ошибка при запросе несуществующего пользователя - проверка 404 статус кода")
    void getNonExistentUserTest() {
        getNonExistentUserTest(23);
    }

    void getNonExistentUserTest(int userId) {
        step("Попытка получить несуществующего пользователя с id =" + userId, () ->
                given(getSingleUserRequestSpec)
                        .when()
                        .get("/users/" + userId)
                        .then()
                        .spec(getSingleUser404ResponseSpec)
        );
    }

    @Test
    @DisplayName("Проверка email 'michael.lawson@reqres.in' в списке пользователей")
    void verifyEmailInUserListTest() {
        UserListResponseModel response = step("Получение списка пользователей на странице 2", () ->
                given(getUserListRequestSpec)
                        .queryParam("page", 2)
                        .when()
                        .get("/users")
                        .then()
                        .spec(getUserListResponseSpec)
                        .extract().as(UserListResponseModel.class)
        );

        step("Проверка наличия email 'michael.lawson@reqres.in' в списке пользователей", () ->
                assertTrue(
                        response.getData().stream()
                                .map(UserDataModel::getEmail)
                                .anyMatch("michael.lawson@reqres.in"::equals)
                )
        );
    }

    @Test
    @DisplayName("Создание пользователя")
    void createUserTest() {
        CreateUserRequestModel requestModel = new CreateUserRequestModel();
        requestModel.setName("morpheus");
        requestModel.setJob("leader");

        CreateUserResponseModel response = step("Отправка запроса на создание пользователя", () ->
                given(createUserRequestSpec)
                        .body(requestModel)
                        .when()
                        .post("/users")
                        .then()
                        .spec(createUserResponseSpec)
                        .extract().as(CreateUserResponseModel.class)
        );

        step("Проверка данных созданного пользователя", () -> {
            assertEquals("morpheus", response.getName());
            assertEquals("leader", response.getJob());
            assertNotNull(response.getId());
        });
    }

    @Test
    @DisplayName("Изменение информации о пользователе")
    void updateUserTest() {
        updateUserTest(2);
    }

    void updateUserTest(int UserId) {
        UpdateUserRequestModel requestModel = new UpdateUserRequestModel();
        requestModel.setName("morpheus");
        requestModel.setJob("zion resident");

        UpdateUserResponseModel response = step("Отправка запроса на обновление пользователя c Id =" + UserId, () ->
                given(updateUserRequestSpec)
                        .body(requestModel)
                        .when()
                        .put("/users/" + UserId)
                        .then()
                        .spec(updateUserResponseSpec)
                        .extract().as(UpdateUserResponseModel.class)
        );

        step("Проверка обновленных данных", () -> {
            assertEquals("morpheus", response.getName());
            assertEquals("zion resident", response.getJob());
        });
    }
}








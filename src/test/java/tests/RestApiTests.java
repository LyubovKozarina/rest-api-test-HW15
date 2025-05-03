package tests;

import io.restassured.RestAssured;
import models.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.CreateUserSpec.createUserRequestSpec;
import static specs.CreateUserSpec.createUserResponseSpec;
import static specs.GetUserListSpec.getUserListRequestSpec;
import static specs.GetUserListSpec.getUserListResponseSpec;
import static specs.GetUserSpec.*;
import static specs.UpdateUserSpec.updateUserRequestSpec;
import static specs.UpdateUserSpec.updateUserResponseSpec;

public class RestApiTests {

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "https://reqres.in";
        RestAssured.basePath = "/api";
    }

    @Test
    @DisplayName("Получение одиночного пользователя по id")
    void getSingleUserTest() {
        SingleUserResponseModel response = step("Получение пользователя по id = 2", () ->
                given(getSingleUserRequestSpec)
                        .when()
                        .get("/users/2")
                        .then()
                        .spec(getSingleUserResponseSpec)
                        .extract().as(SingleUserResponseModel.class));

        step("Проверка данных пользователя", () -> {
            assertThat(response.getData().getId()).isEqualTo(2);
            assertThat(response.getData().getEmail()).contains("@reqres.in");
        });
    }

    @Test
    @DisplayName("Ошибка при запросе несуществующего пользователя - проверка 404 статус кода")
    void getNonExistentUserTest() {
        step("Попытка получить несуществующего пользователя с id = 23", () ->
                given(getSingleUserRequestSpec)
                        .when()
                        .get("/users/23")
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
                assertThat(response.getData())
                        .extracting(UserDataModel::getEmail)
                        .contains("michael.lawson@reqres.in")
        );
    }

    @Test
    @DisplayName("Создание пользователя")
    void createUserTest() {
        UserActionModel requestModel = new UserActionModel();
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
            assertThat(response.getName()).isEqualTo("morpheus");
            assertThat(response.getJob()).isEqualTo("leader");
            assertThat(response.getId()).isNotNull();
        });
    }

    @Test
    @DisplayName("Изменение информации о пользователе")
    void updateUserTest() {
        UserActionModel requestModel = new UserActionModel();
        requestModel.setName("morpheus");
        requestModel.setJob("zion resident");

        UserActionModel response = step("Отправка запроса на обновление пользователя c Id = 2", () ->
                given(updateUserRequestSpec)
                        .body(requestModel)
                        .when()
                        .put("/users/2")
                        .then()
                        .spec(updateUserResponseSpec)
                        .extract().as(UserActionModel.class)
        );

        step("Проверка обновленных данных", () -> {
            assertThat(response.getName()).isEqualTo("morpheus");
            assertThat(response.getJob()).isEqualTo("zion resident");
        });
    }
}








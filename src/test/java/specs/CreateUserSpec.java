package specs;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static helpers.CustomAllureListener.withCustomTemplates;
import static io.restassured.RestAssured.with;
import static io.restassured.filter.log.LogDetail.*;

public class CreateUserSpec {

    public static RequestSpecification createUserRequestSpec = with()
            .filter(withCustomTemplates())
            .header("x-api-key", "reqres-free-v1")
            .contentType("application/json")
            .log().uri()
            .log().body();

    public static ResponseSpecification createUserResponseSpec = new ResponseSpecBuilder()
            .expectStatusCode(201)
            .log(STATUS)
            .log(BODY)
            .build();
}

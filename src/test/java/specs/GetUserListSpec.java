package specs;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static helpers.CustomAllureListener.withCustomTemplates;
import static io.restassured.RestAssured.with;
import static io.restassured.filter.log.LogDetail.BODY;
import static io.restassured.filter.log.LogDetail.STATUS;

public class GetUserListSpec {

    public static RequestSpecification getUserListRequestSpec = with()
            .filter(withCustomTemplates())
            .header("x-api-key", "reqres-free-v1")
            .log().uri()
            .log().headers();

    public static ResponseSpecification getUserListResponseSpec = new ResponseSpecBuilder()
            .expectStatusCode(200)
            .log(STATUS)
            .log(BODY)
            .build();
}

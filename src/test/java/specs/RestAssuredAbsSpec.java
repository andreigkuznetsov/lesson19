package specs;

import io.restassured.specification.RequestSpecification;

import static filters.CustomLogFilter.customLogFilter;
import static io.restassured.RestAssured.with;

public class RestAssuredAbsSpec {
    static public RequestSpecification requestSpec =
            with()
                    .filter(customLogFilter().withCustomTemplates())
                    .log().uri()
                    .log().body();

}




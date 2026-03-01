package com.api_test.api_test.tests;

import com.api_test.api_test.base.BaseTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;

/**
 * HomeTest — kiểm tra API trang chủ OPhim.
 * Endpoint: GET /v1/api/home
 */
@DisplayName("🏠 API Trang Chủ")
public class HomeTest extends BaseTest {

    @Test
    @DisplayName("GET /v1/api/home → status 200 và trả về dữ liệu hợp lệ")
    public void testHomePage_Returns200AndHasItems() {
        RestAssured
            .given()
                .spec(requestSpec)
            .when()
                .get("/v1/api/home")
            .then()
                .log().ifError()
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("data", notNullValue())
                .body("data.items", notNullValue());

        System.out.println("[PASS] GET /v1/api/home → 200 OK, data.items hiện diện");
    }

    @Test
    @DisplayName("GET /v1/api/home → response time dưới 5 giây")
    public void testHomePage_ResponseTimeUnder5s() {
        Response response = RestAssured
            .given()
                .spec(requestSpec)
            .when()
                .get("/v1/api/home")
            .then()
                .statusCode(200)
                .extract().response();

        long timeMs = response.getTime();
        System.out.println("[INFO] Response time trang chủ: " + timeMs + "ms");
        assert timeMs < 5000 : "Response time quá chậm: " + timeMs + "ms";
    }
}

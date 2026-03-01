package com.api_test.api_test.tests;

import com.api_test.api_test.base.BaseTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * HealthCheckTest — kiểm tra kết nối cơ bản đến API OPhim.
 * Đây là test đầu tiên cần pass trước khi chạy các test khác.
 */
@DisplayName("🟢 Health Check — Kết Nối API")
public class HealthCheckTest extends BaseTest {

    @Test
    @DisplayName("GET /v1/api/home → API có thể kết nối và phản hồi")
    public void testApiIsReachable() {
        RestAssured
            .given()
                .spec(requestSpec)
            .when()
                .get("/v1/api/home")
            .then()
                .log().ifError()
                .statusCode(200);

        System.out.println("[PASS] API OPhim đang hoạt động và có thể kết nối");
    }

    @Test
    @DisplayName("GET /v1/api/home → Content-Type là application/json")
    public void testApiReturnsJson() {
        RestAssured
            .given()
                .spec(requestSpec)
            .when()
                .get("/v1/api/home")
            .then()
                .statusCode(200)
                .contentType(org.hamcrest.Matchers.containsStringIgnoringCase("application/json"));

        System.out.println("[PASS] API trả về Content-Type: application/json");
    }

    @Test
    @DisplayName("GET /v1/api/home → response body không rỗng")
    public void testApiResponseBodyNotEmpty() {
        Response response = RestAssured
            .given()
                .spec(requestSpec)
            .when()
                .get("/v1/api/home")
            .then()
                .statusCode(200)
                .extract().response();

        String body = response.getBody().asString();
        assertThat("Response body không được rỗng", body, not(emptyOrNullString()));
        assertThat("Response body phải chứa 'status'", body, containsString("status"));
        System.out.println("[PASS] Response body hợp lệ, chứa trường 'status'");
    }
}
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
 * MovieTest — cấu trúc JSON thực tế của OPhim:
 *   GET /v1/api/phim/{slug} → { status, message, data: { item: {...}, episodes: [...] } }
 *   GET /v1/api/phim/{slug}/peoples → { status, message, data: [...] }
 */
@DisplayName("🎭 Movie Test")
public class MovieTest extends BaseTest {

    private static final String MOVIE_SLUG_SERIES = "loi-nhan-cuoi-cung-phan-2";
    private static final String MOVIE_SLUG_SINGLE = "ke-cap-mat-trang-3";
    private static final String MOVIE_SLUG_ACTION  = "dac-vu-mat";

    @Test
    @DisplayName("GET /v1/api/phim/{slug}/peoples → status 200 và có data")
    public void testGetMoviePeoples() {
        Response response = RestAssured
            .given()
                .spec(requestSpec)
            .when()
                .get("/v1/api/phim/" + MOVIE_SLUG_SERIES + "/peoples")
            .then()
                .log().ifError()
                .statusCode(200)
                .extract().response();

        Object data = response.jsonPath().get("data");
        assertThat("data phải không null", data, notNullValue());
        System.out.println("[PASS] /peoples → OK");
    }

    @Test
    @DisplayName("GET /v1/api/phim/ke-cap-mat-trang-3 → status 200 và data.item.slug khớp")
    public void testGetMovieDetail_SlugMatch() {
        // Cấu trúc thực: status, data.item.slug
        RestAssured
            .given()
                .spec(requestSpec)
            .when()
                .get("/v1/api/phim/" + MOVIE_SLUG_SINGLE)
            .then()
                .log().ifError()
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("data.item.slug", equalTo(MOVIE_SLUG_SINGLE));

        System.out.println("[PASS] GET /v1/api/phim/" + MOVIE_SLUG_SINGLE + " → slug khớp");
    }

    @Test
    @DisplayName("GET /v1/api/phim/dac-vu-mat → kiểm tra các trường cơ bản tại data.item")
    public void testGetMovieDetail_BasicFields() {
        RestAssured
            .given()
                .spec(requestSpec)
            .when()
                .get("/v1/api/phim/" + MOVIE_SLUG_ACTION)
            .then()
                .log().ifError()
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("data.item.name", not(emptyOrNullString()))
                .body("data.item.origin_name", not(emptyOrNullString()))
                .body("data.item.type", notNullValue())
                .body("data.item.year", greaterThan(0));

        System.out.println("[PASS] GET /v1/api/phim/" + MOVIE_SLUG_ACTION + " → data.item hợp lệ");
    }

    @Test
    @DisplayName("GET /v1/api/danh-sach/phim-moi → có ít nhất 1 phim trong items")
    public void testLatestMovies_NotEmpty() {
        Response response = RestAssured
            .given()
                .spec(requestSpec)
                .queryParam("page", 1)
            .when()
                .get("/v1/api/danh-sach/phim-moi")
            .then()
                .statusCode(200)
                .body("data.items", notNullValue())
                .extract().response();

        List<?> items = response.jsonPath().getList("data.items");
        assertThat("Phim mới phải có ít nhất 1 item", items.size(), greaterThan(0));
        System.out.println("[PASS] Phim mới có " + items.size() + " phim trên trang 1");
    }
}
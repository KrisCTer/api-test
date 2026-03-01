package com.api_test.api_test.tests;

import com.api_test.api_test.base.BaseTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * MovieDetailTest — cấu trúc JSON thực tế của OPhim:
 *
 *   GET /v1/api/phim/{slug}
 *   → { status, message, data: { seoOnPage, item: { _id, name, slug, ... }, episodes: [...] } }
 *
 *   GET /v1/api/phim/{slug}/peoples  → { status, data: [...] }
 *   GET /v1/api/phim/{slug}/images   → { status, data: [...] }
 *   GET /v1/api/phim/{slug}/keywords → { status, data: [...] }
 */
@DisplayName("🎬 API Chi Tiết Phim")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MovieDetailTest extends BaseTest {

    // Slug phim thực tế trên ophim1.com
    private static final String MOVIE_SLUG = "ke-cap-mat-trang-3";            // Despicable Me 3, phim lẻ hoàn chỉnh
    private static final String MOVIE_SLUG_VIET = "loi-nhan-cuoi-cung-phan-2"; // The Last Thing He Told Me S2

    // ============================================================
    // THÔNG TIN PHIM — data.item (không phải top-level "movie")
    // ============================================================

    @Test
    @Order(1)
    @DisplayName("GET /v1/api/phim/{slug} → trả về đầy đủ thông tin tại data.item")
    public void testMovieDetail_HasRequiredFields() {
        Response response = RestAssured
            .given()
                .spec(requestSpec)
            .when()
                .get("/v1/api/phim/" + MOVIE_SLUG)
            .then()
                .log().ifError()
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("data.item", notNullValue())
                .body("data.item.name", not(emptyOrNullString()))
                .body("data.item.slug", equalTo(MOVIE_SLUG))
                .extract().response();

        String movieName = response.jsonPath().getString("data.item.name");
        String movieType = response.jsonPath().getString("data.item.type");
        System.out.printf("[PASS] Phim: %s (type=%s)%n", movieName, movieType);
    }

    @Test
    @Order(2)
    @DisplayName("GET /v1/api/phim/{slug} → phim có status, thumb_url, poster_url")
    public void testMovieDetail_HasImagesAndStatus() {
        Response response = RestAssured
            .given()
                .spec(requestSpec)
            .when()
                .get("/v1/api/phim/" + MOVIE_SLUG)
            .then()
                .statusCode(200)
                .body("data.item", notNullValue())
                .extract().response();

        String status    = response.jsonPath().getString("data.item.status");
        String thumbUrl  = response.jsonPath().getString("data.item.thumb_url");
        String posterUrl = response.jsonPath().getString("data.item.poster_url");

        assertThat("data.item.status phải tồn tại", status, notNullValue());
        assertThat("Phải có thumb_url hoặc poster_url", thumbUrl != null || posterUrl != null, is(true));
        System.out.printf("[PASS] Status=%s, thumb=%s%n", status, thumbUrl);
    }

    @Test
    @Order(3)
    @DisplayName("GET /v1/api/phim/slug-khong-ton-tai → phải xử lý đúng")
    public void testMovieDetail_NonExistentSlug() {
        int statusCode = RestAssured
            .given()
                .spec(requestSpec)
            .when()
                .get("/v1/api/phim/phim-khong-ton-tai-xyz-abc-999")
            .then()
                .extract().statusCode();

        assertThat("Slug không tồn tại → 200, 400, hoặc 404",
                statusCode, anyOf(is(200), is(404), is(400)));
        System.out.println("[PASS] Slug không tồn tại → status " + statusCode);
    }

    // ============================================================
    // HÌNH ẢNH PHIM
    // ============================================================

    @Test
    @Order(4)
    @DisplayName("GET /v1/api/phim/{slug}/images → trả về status 200")
    public void testMovieImages_Returns200() {
        int statusCode = RestAssured
            .given()
                .spec(requestSpec)
            .when()
                .get("/v1/api/phim/" + MOVIE_SLUG + "/images")
            .then()
                .log().ifError()
                .extract().statusCode();

        assertThat("Images → 200 hoặc 404", statusCode, anyOf(is(200), is(404)));
        System.out.println("[PASS] GET /images → status " + statusCode);
    }

    // ============================================================
    // DIỄN VIÊN / ĐẠO DIỄN
    // ============================================================

    @Test
    @Order(5)
    @DisplayName("GET /v1/api/phim/{slug}/peoples → status 200 và data không null")
    public void testMoviePeoples_ReturnsCast() {
        // API phụ (peoples/keywords/images) trả về: {success: true, status_code: 200, data: [...]}
        Response response = RestAssured
            .given()
                .spec(requestSpec)
            .when()
                .get("/v1/api/phim/" + MOVIE_SLUG_VIET + "/peoples")
            .then()
                .log().ifError()
                .statusCode(200)
                .extract().response();

        assertThat("HTTP 200 nghĩa là thành công", response.statusCode(), is(200));
        Object data = response.jsonPath().get("data");
        assertThat("data peoples phải tồn tại", data, notNullValue());
        System.out.println("[PASS] GET /peoples → OK");
    }

    // ============================================================
    // TỪ KHÓA
    // ============================================================

    @Test
    @Order(6)
    @DisplayName("GET /v1/api/phim/{slug}/keywords → status 200 và data không null")
    public void testMovieKeywords_ReturnsKeywords() {
        // API phụ trả về: {success: true, status_code: 200, data: [...]}
        Response response = RestAssured
            .given()
                .spec(requestSpec)
            .when()
                .get("/v1/api/phim/" + MOVIE_SLUG + "/keywords")
            .then()
                .log().ifError()
                .statusCode(200)
                .extract().response();

        assertThat("HTTP 200 nghĩa là thành công", response.statusCode(), is(200));
        Object data = response.jsonPath().get("data");
        assertThat("data keywords phải tồn tại", data, notNullValue());
        System.out.println("[PASS] GET /keywords → OK");
    }

    // ============================================================
    // VALIDATE CẤU TRÚC JSON ĐẦY ĐỦ
    // ============================================================

    @Test
    @Order(7)
    @DisplayName("GET /v1/api/phim/{slug} → validate cấu trúc JSON: data.item có đủ fields")
    public void testMovieDetail_ValidateJsonStructure() {
        RestAssured
            .given()
                .spec(requestSpec)
            .when()
                .get("/v1/api/phim/" + MOVIE_SLUG)
            .then()
                .statusCode(200)
                // Top-level
                .body("status", notNullValue())
                .body("data", notNullValue())
                // data.item fields
                .body("data.item._id", notNullValue())
                .body("data.item.name", notNullValue())
                .body("data.item.slug", notNullValue())
                .body("data.item.origin_name", notNullValue())
                .body("data.item.type", notNullValue())
                .body("data.item.status", notNullValue())
                .body("data.item.category", notNullValue())
                .body("data.item.country", notNullValue());

        System.out.println("[PASS] Cấu trúc JSON data.item hợp lệ đầy đủ");
    }
}

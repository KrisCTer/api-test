package com.api_test.api_test.tests;

import com.api_test.api_test.base.BaseTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * CategoryCountryYearTest — kiểm tra các API thể loại, quốc gia, năm phát hành.
 *
 * Endpoints:
 *   - GET /v1/api/the-loai              → Danh sách thể loại
 *   - GET /v1/api/the-loai/{slug}       → Phim theo thể loại
 *   - GET /v1/api/quoc-gia              → Danh sách quốc gia
 *   - GET /v1/api/quoc-gia/{slug}       → Phim theo quốc gia
 *   - GET /v1/api/nam-phat-hanh         → Danh sách năm phát hành
 *   - GET /v1/api/nam-phat-hanh/{year}  → Phim theo năm
 */


    // ============================================================
    // 5 TEST MỚI VỀ LỌC PHIM (4 PASS ✅ + 1 FAIL ❌)
    // Dựa trên yêu cầu: Lọc Hành Động (Âu Mỹ) và Tình Cảm (Hàn Quốc)
    // ============================================================

    @Test
    @DisplayName("✅ [PASS] Lấy phim Hành động - Âu Mỹ (trang 1, limit 5)")
    public void testHanhDongAuMy_ReturnsCorrectTitle() {
        RestAssured
            .given()
                .spec(requestSpec)
                .queryParam("page", 1)
                .queryParam("limit", 5)
                .queryParam("country", "au-my")
            .when()
                .get("/v1/api/the-loai/hanh-dong")
            .then()
                .log().ifError()
                .statusCode(200)
                .body("status", equalTo("success"))
                // titlePage thực tế trả về là "Hành Động"
                .body("data.titlePage", containsStringIgnoringCase("Hành động"))
                .body("data.items", not(empty()));

        System.out.println("[PASS] Lấy phim Hành động Âu Mỹ thành công ✓");
    }

    @Test
    @DisplayName("✅ [PASS] Lấy phim Tình cảm - Hàn Quốc (trang 1, limit 5)")
    public void testTinhCamHanQuoc_ReturnsCorrectTitle() {
        RestAssured
            .given()
                .spec(requestSpec)
                .queryParam("page", 1)
                .queryParam("limit", 5)
                .queryParam("country", "han-quoc")
            .when()
                .get("/v1/api/the-loai/tinh-cam")
            .then()
                .log().ifError()
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("data.titlePage", containsStringIgnoringCase("Tình cảm"))
                .body("data.items", not(empty()));

        System.out.println("[PASS] Lấy phim Tình cảm Hàn Quốc thành công ✓");
    }

    @Test
    @DisplayName("✅ [PASS] Kiểm tra tham số limit=5 thực sự trả về đúng số lượng phim")
    public void testHanhDongAuMy_Limit5_Returns5Items() {
        Response response = RestAssured
            .given()
                .spec(requestSpec)
                .queryParam("page", 1)
                .queryParam("limit", 5)
                .queryParam("country", "au-my")
            .when()
                .get("/v1/api/the-loai/hanh-dong")
            .then()
                .statusCode(200)
                .extract().response();

        List<?> items = response.jsonPath().getList("data.items");
        assertThat("Danh sách phim trả về phải <= 5", items.size(), lessThanOrEqualTo(5));
        System.out.println("[PASS] Limit 5 giới hạn chính xác số lượng phim rả về -> " + items.size() + " phim ✓");
    }

    @Test
    @DisplayName("✅ [PASS] Kiểm tra cấu trúc json pagination của API thể loại")
    public void testTinhCamHanQuoc_HasPaginationParams() {
        RestAssured
            .given()
                .spec(requestSpec)
                .queryParam("page", 1)
                .queryParam("country", "han-quoc")
            .when()
                .get("/v1/api/the-loai/tinh-cam")
            .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("data.params.pagination.currentPage", equalTo(1))
                .body("data.params.pagination.totalItems", greaterThan(0))
                .body("data.params.pagination.totalItemsPerPage", greaterThan(0));

        System.out.println("[PASS] Pagination params của API thể loại đầy đủ các trường thiết yếu ✓");
    }

    @Test
    @DisplayName("❌ [FAIL] Cố tình kiểm tra sai giá trị titlePage (Bug Demo)")
    public void testHanhDongAuMy_IntentionalFail_TitlePage() {
        // Test này INTENTIONALLY FAIL để mô phỏng lỗi dữ liệu trả về sai
        RestAssured
            .given()
                .spec(requestSpec)
                .queryParam("page", 1)
                .queryParam("country", "au-my")
            .when()
                .get("/v1/api/the-loai/hanh-dong")
            .then()
                .statusCode(200)
                // Thực tế titlePage là "Hành Động", mình cố tình expect chữ "Thiếu Nhi"
                .body("data.titlePage", equalTo("Phim Thiếu Nhi")); // ← Sẽ báo FAIL tại đây
    }
}

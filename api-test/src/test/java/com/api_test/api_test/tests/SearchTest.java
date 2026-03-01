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
 * SearchTest — kiểm tra API tìm kiếm phim.
 * Endpoint: GET /v1/api/tim-kiem?keyword={keyword}&page={page}
 */
@DisplayName("🔍 API Tìm Kiếm Phim")
public class SearchTest extends BaseTest {

    // @Test
    // @DisplayName("Tìm kiếm từ khóa hợp lệ 'avengers' → trả về kết quả")
    // public void testSearch_ValidKeyword_ReturnsResults() {
    //     Response response = RestAssured
    //         .given()
    //             .spec(requestSpec)
    //             .queryParam("keyword", "avengers")
    //             .queryParam("page", 1)
    //             .queryParam("limit", 5) // Giới hạn 5 kết quả cho nhẹ
    //         .when()
    //             .get("/v1/api/tim-kiem")
    //         .then()
    //             .log().ifError()
    //             .statusCode(200)
    //             .body("status", equalTo("success"))
    //             .body("data", notNullValue())
    //             .extract().response();

    //     List<?> items = response.jsonPath().getList("data.items");
    //     assertThat("Tìm kiếm 'avengers' phải có kết quả", items, notNullValue());
    //     System.out.println("[PASS] Tìm kiếm 'avengers' → " + items.size() + " kết quả");
    // }

    // @Test
    // @DisplayName("Tìm kiếm tiếng Việt 'doraemon' → trả về kết quả")
    // public void testSearch_VietnameseKeyword_ReturnsResults() {
    //     Response response = RestAssured
    //         .given()
    //             .spec(requestSpec)
    //             .queryParam("keyword", "doraemon")
    //             .queryParam("limit", 5) // Giới hạn 5 kết quả cho nhẹ
    //         .when()
    //             .get("/v1/api/tim-kiem")
    //         .then()
    //             .statusCode(200)
    //             .extract().response();

    //     int totalItems = response.jsonPath().getInt("data.params.pagination.totalItems");
    //     System.out.println("[PASS] Tìm kiếm 'doraemon' → tổng " + totalItems + " kết quả");
    // }

    // @Test
    // @DisplayName("Tìm kiếm không có từ khóa → phải xử lý đúng (không crash)")
    // public void testSearch_NoKeyword_HandledGracefully() {
    //     int statusCode = RestAssured
    //         .given()
    //             .spec(requestSpec)
    //         .when()
    //             .get("/v1/api/tim-kiem")
    //         .then()
    //             .extract().statusCode();

    //     assertThat("Không keyword → 200, 400, hoặc 422",
    //             statusCode, anyOf(is(200), is(400), is(422)));
    //     System.out.println("[PASS] Không có keyword → status " + statusCode + " (expected)");
    // }

    // @Test
    // @DisplayName("Tìm kiếm từ khóa ngắn 1 ký tự → API xử lý đúng")
    // public void testSearch_SingleCharKeyword_HandledGracefully() {
    //     int statusCode = RestAssured
    //         .given()
    //             .spec(requestSpec)
    //             .queryParam("keyword", "a")
    //         .when()
    //             .get("/v1/api/tim-kiem")
    //         .then()
    //             .extract().statusCode();

    //     // Từ khóa quá ngắn → có thể trả 400 hoặc 200 với kết quả rỗng
    //     assertThat("1 ký tự → 200 hoặc 400",
    //             statusCode, anyOf(is(200), is(400)));
    //     System.out.println("[PASS] Từ khóa 1 ký tự → status " + statusCode);
    // }

    // @Test
    // @DisplayName("Tìm kiếm phân trang — trang 2 khác trang 1")
    // public void testSearch_Pagination_Page2DiffersFromPage1() {
    //     Response page1 = RestAssured
    //         .given()
    //             .spec(requestSpec)
    //             .queryParam("keyword", "naruto")
    //             .queryParam("page", 1)
    //             .queryParam("limit", 5) // Set limit=5 thì phim ở mỗi trang chỉ max 5
    //         .when()
    //             .get("/v1/api/tim-kiem")
    //         .then()
    //             .statusCode(200)
    //             .extract().response();

    //     Response page2 = RestAssured
    //         .given()
    //             .spec(requestSpec)
    //             .queryParam("keyword", "naruto")
    //             .queryParam("page", 2)
    //             .queryParam("limit", 5) // Phải đồng bộ limit=5 với page 1
    //         .when()
    //             .get("/v1/api/tim-kiem")
    //         .then()
    //             .statusCode(200)
    //             .extract().response();

    //     List<?> items1 = page1.jsonPath().getList("data.items");
    //     List<?> items2 = page2.jsonPath().getList("data.items");

    //     // Ít nhất trang 1 phải có kết quả
    //     assertThat("Trang 1 phải có kết quả cho 'naruto'", items1, not(empty()));
    //     System.out.printf("[PASS] Naruto: trang 1=%d, trang 2=%d phim%n",
    //             items1.size(), items2 != null ? items2.size() : 0);
    // }

    // ============================================================
    // 3 TEST MỚI: 2 PASS ✅ + 1 FAIL ❌ (có chủ đích)
    // Dựa trên response thực từ API doc:
    //   data.params.pagination = { currentPage, totalItems, totalItemsPerPage, totalPages }
    // ============================================================

    @Test
    @DisplayName("✅ [PASS] limit=3 → chỉ trả về tối đa 3 kết quả tìm kiếm")
    public void testSearch_LimitParam_ReturnsCorrectCount() {
        // Search API có hỗ trợ param limit theo API doc (mặc định 24)
        Response response = RestAssured
            .given()
                .spec(requestSpec)
                .queryParam("keyword", "avengers")
                .queryParam("page", 1)
                .queryParam("limit", 3)
            .when()
                .get("/v1/api/tim-kiem")
            .then()
                .log().ifError()
                .statusCode(200)
                .body("status", equalTo("success"))
                .extract().response();

        List<?> items = response.jsonPath().getList("data.items");
        assertThat("limit=3 → tối đa 3 kết quả", items.size(), lessThanOrEqualTo(3));
        System.out.println("[PASS] limit=3 → trả về " + items.size() + " kết quả");
    }

    @Test
    @DisplayName("✅ [PASS] titlePage chứa từ khóa tìm kiếm")
    public void testSearch_TitlePage_ContainsKeyword() {
        RestAssured
            .given()
                .spec(requestSpec)
                .queryParam("keyword", "avengers")
            .when()
                .get("/v1/api/tim-kiem")
            .then()
                .statusCode(200)
                .body("data.titlePage", containsStringIgnoringCase("avengers"));

        System.out.println("[PASS] titlePage có chứa đúng từ khóa ✓");
    }

    @Test
    @DisplayName("❌ [FAIL] Cố tình kiểm tra sai giá trị message")
    public void testSearch_Message_ExpectFail() {
        // Test này INTENTIONALLY FAIL để xem báo lỗi khi trường "message" không đúng
        RestAssured
            .given()
                .spec(requestSpec)
                .queryParam("keyword", "avengers")
            .when()
                .get("/v1/api/tim-kiem")
            .then()
                .statusCode(200)
                // Thực tế message là "Tìm kiếm thành công", test mong muốn chữ khác để ép Fail
                .body("message", equalTo("oh no da loi")); // ← FAIL tại đây
    }
}

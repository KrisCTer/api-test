package com.api_test.api_test.tests;

import com.api_test.api_test.base.BaseTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.Matchers.*;

/**
 * HomeTest — chi tiết kiểm tra API trang chủ OPhim (GET /v1/api/home).
 * Lệnh test:
 * mvn test -Dtest=HomeTest
 * 
 * CÁC CÔNG VIỆC ĐÃ THỰC HIỆN:
 * -----------------------------------------------------------------------------------------
 * 1. KIỂM TRA PHẢN HỒI CƠ BẢN:
 * - Xác nhận HTTP Status Code 200 (Thành công).
 * - Kiểm tra cấu trúc JSON gốc: "status" == "success", có "message" và "data".
 * 
 * 2. KIỂM TRA SEO & METADATA (data.seoOnPage):
 * - Đảm bảo "titleHead" và "descriptionHead" không trống để phục vụ SEO.
 * 
 * 3. KIỂM TRA NỘI DUNG PHIM (data.items):
 * - Xác nhận danh sách phim không null.
 * - Kiểm tra các trường bắt buộc của mỗi bộ phim: "_id", "name" (tên tiếng
 * Việt),
 * "slug" (đường dẫn), và "thumb_url" (hình ảnh đại diện).
 * 
 * 4. KIỂM TRA PHÂN TRANG (data.params.pagination):
 * - Xác nhận các thông số: "currentPage", "totalItems" (tổng số phim),
 * và "totalItemsPerPage" (số phim mỗi trang).
 * 
 * 5. KIỂM TRA HẠ TẦNG (Domains):
 * - Kiểm tra sự tồn tại của APP_DOMAIN_CDN_IMAGE (host ảnh) và
 * APP_DOMAIN_FRONTEND.
 * 
 * 6. KIỂM TRA HIỆU NĂNG:
 * - Xác nhận thời gian phản hồi (Response Time) phải dưới 5 giây.
 * 
 * 7. TEST FAIL (DEMO): Kiểm tra sai trạng thái (Kỳ vọng: "error").
 * 8. TEST FAIL (DEMO): Kiểm tra sai tiêu đề SEO (Kỳ vọng: Title không khớp).
 * 9. TEST FAIL (DEMO): Kiểm tra sai tổng số lượng phim (Kỳ vọng: 0).
 * 
 * - Ý nghĩa: Kiểm chứng hệ thống báo cáo lỗi khi dữ liệu thực tế không khớp với
 * kỳ vọng.
 * - Trạng thái: 3 test này hiện đã được comment lại ở cuối file để đảm bảo dự
 * án
 * luôn Build Success.
 * -----------------------------------------------------------------------------------------
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
    @DisplayName("GET /v1/api/home → validate cấu trúc JSON cơ bản (status, message, data)")
    public void testHomePage_HasRequiredJsonStructure() {
        RestAssured
                .given()
                .spec(requestSpec)
                .when()
                .get("/v1/api/home")
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("message", notNullValue())
                .body("data", notNullValue());
    }

    @Test
    @DisplayName("GET /v1/api/home → check SEO: titleHead và descriptionHead")
    public void testHomePage_HasSeoOnPage() {
        RestAssured
                .given()
                .spec(requestSpec)
                .when()
                .get("/v1/api/home")
                .then()
                .statusCode(200)
                .body("data.seoOnPage.titleHead", not(emptyOrNullString()))
                .body("data.seoOnPage.descriptionHead", not(emptyOrNullString()));
    }

    @Test
    @DisplayName("GET /v1/api/home → kiểm tra các trường bắt buộc của phim trong items")
    public void testHomePage_ItemsHaveRequiredFields() {
        Response response = RestAssured
                .given()
                .spec(requestSpec)
                .when()
                .get("/v1/api/home")
                .then()
                .statusCode(200)
                .extract().response();

        // Kiểm tra item đầu tiên (nếu có)
        List<?> items = response.jsonPath().getList("data.items");
        if (items != null && !items.isEmpty()) {
            RestAssured
                    .given()
                    .spec(requestSpec)
                    .when()
                    .get("/v1/api/home")
                    .then()
                    .body("data.items[0]._id", notNullValue())
                    .body("data.items[0].name", notNullValue())
                    .body("data.items[0].slug", notNullValue())
                    .body("data.items[0].thumb_url", notNullValue());
            System.out.println("[PASS] Item đầu tiên có đầy đủ các trường bắt buộc (_id, name, slug, thumb_url)");
        }
    }

    @Test
    @DisplayName("GET /v1/api/home → kiểm tra thông số phân trang (pagination)")
    public void testHomePage_HasPaginationParams() {
        RestAssured
                .given()
                .spec(requestSpec)
                .when()
                .get("/v1/api/home")
                .then()
                .statusCode(200)
                .body("data.params.pagination.currentPage", equalTo(1))
                .body("data.params.pagination.totalItems", greaterThanOrEqualTo(0))
                .body("data.params.pagination.totalItemsPerPage", greaterThan(0));
    }

    @Test
    @DisplayName("GET /v1/api/home → check CDN Domain và Frontend Domain")
    public void testHomePage_HasDomains() {
        RestAssured
                .given()
                .spec(requestSpec)
                .when()
                .get("/v1/api/home")
                .then()
                .statusCode(200)
                .body("data.APP_DOMAIN_CDN_IMAGE", not(emptyOrNullString()))
                .body("data.APP_DOMAIN_FRONTEND", not(emptyOrNullString()));
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

    @Test
    @DisplayName("✅ [PASS] Home: Kiểm tra Banner Items (ảnh đại diện trang chủ)")
    public void testHome_BannerItems() {
        Response response = RestAssured
                .given()
                .spec(requestSpec)
                .when()
                .get("/v1/api/home")
                .then()
                .statusCode(200)
                .body("data.items", notNullValue())
                .extract().response();

        List<?> items = response.jsonPath().getList("data.items");
        if (items != null && !items.isEmpty()) {
            // Kiểm tra item đầu tiên phải có ảnh đại diện (thumb_url) để hiển thị Banner
            String thumbUrl = response.jsonPath().getString("data.items[0].thumb_url");
            String posterUrl = response.jsonPath().getString("data.items[0].poster_url");
            assert (thumbUrl != null || posterUrl != null) : "Banner item phải có ảnh đại diện";
            System.out.println("[PASS] Home: Banner có " + items.size() + " phim, ảnh đại diện hợp lệ ✓");
        }
    }

    @Test
    @DisplayName("✅ [PASS] Home: Kiểm tra danh sách phim mới nhất (Latest Movies)")
    public void testHome_LatestMovies() {
        Response response = RestAssured
                .given()
                .spec(requestSpec)
                .when()
                .get("/v1/api/home")
                .then()
                .statusCode(200)
                .body("data.items", notNullValue())
                .extract().response();

        List<?> items = response.jsonPath().getList("data.items");
        assert items != null && !items.isEmpty() : "Trang chủ phải có ít nhất 1 phim mới";

        // Kiểm tra phim đầu tiên có đầy đủ thông tin cơ bản
        String name = response.jsonPath().getString("data.items[0].name");
        String slug = response.jsonPath().getString("data.items[0].slug");
        assert name != null && !name.isEmpty() : "Phim mới phải có tên";
        assert slug != null && !slug.isEmpty() : "Phim mới phải có slug";

        System.out.println("[PASS] Home: Có " + items.size() + " phim mới, phim đầu: '" + name + "' ✓");
    }

    // ============================================================
    // 3 TEST CỐ TÌNH SAI (FAIL DEMO ❌)
    // ============================================================

    // @Test
    // @DisplayName("❌ [FAIL] Cố tình kiểm tra sai status (Expect error)")
    // public void testHomePage_IntentionalFail_Status() {
    // // LÝ DO FAIL: API thực tế trả về "success", nhưng test này kỳ vọng "error".
    // RestAssured
    // .given()
    // .spec(requestSpec)
    // .when()
    // .get("/v1/api/home")
    // .then()
    // .statusCode(200)
    // .body("status", equalTo("error"));
    // }

    // @Test
    // @DisplayName("❌ [FAIL] Cố tình kiểm tra sai title SEO")
    // public void testHomePage_IntentionalFail_SeoTitle() {
    // // LÝ DO FAIL: Tiêu đề thật từ API là mô tả phim, không phải chuỗi "Trang Web
    // Phim Lậu Số 1".
    // RestAssured
    // .given()
    // .spec(requestSpec)
    // .when()
    // .get("/v1/api/home")
    // .then()
    // .statusCode(200)
    // .body("data.seoOnPage.titleHead", equalTo("Trang Web Phim Lậu Số 1"));
    // }

    // @Test
    // @DisplayName("❌ [FAIL] Cố tình kiểm tra số lượng phim = 0")
    // public void testHomePage_IntentionalFail_TotalItems() {
    // // LÝ DO FAIL: API trả về tổng số lượng phim thực tế (> 34,000), không phải
    // 0.
    // RestAssured
    // .given()
    // .spec(requestSpec)
    // .when()
    // .get("/v1/api/home")
    // .then()
    // .statusCode(200)
    // .body("data.params.pagination.totalItems", equalTo(0));
    // }
}

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
 * CategoryCountryYearTest — kiểm tra các API thể loại, quốc gia, năm phát hành.
 *
 * CÁC NỘI DUNG ĐÃ THỰC HIỆN:
 * 1. [Thể loại - Category]
 * - Lấy phim Hành động (Âu Mỹ) thành công.
 * - Lấy phim Tình cảm (Hàn Quốc) thành công.
 * - Xác thực tham số 'limit=5' giới hạn đúng số lượng phim trả về.
 * - Xác thực cấu trúc dữ liệu Pagination (currentPage, totalItems...).
 * - (Demo Fail) Kiểm tra sai giá trị titlePage (đã comment).
 *
 * 2. [Quốc gia - Country]
 * - Lấy danh sách tất cả các quốc gia hệ thống hỗ trợ.
 * - Lấy phim theo quốc gia 'han-quoc' thành công.
 * - Lấy phim theo quốc gia 'trung-quoc' kết hợp lọc thể loại 'co-trang'.
 * - Xác thực chuyển trang (page=2) cho kết quả chính xác.
 * - (Demo Fail) Kiểm tra sai status code 404 (đã comment).
 *
 * 3. [Năm phát hành - Year]
 * - Lấy danh sách tất cả các năm phát hành phim.
 * - Lấy danh sách phim phát hành năm '2025' thành công.
 * - Lấy phim phát hành năm '2024' kết hợp lọc thể loại 'hanh-dong'.
 * - Xác thực số lượng phim trả về trong trang đầu tiên.
 * - (Demo Fail) Kiểm tra sai giá trị 'status' (đã comment).
 */
@DisplayName("📂 API Thể Loại, Quốc Gia, Năm")
public class CategoryCountryYearTest extends BaseTest {

    // ============================================================
    // 📂 SECTION 1: THỂ LOẠI (CATEGORY) - 5 TESTS (+1 LIST ALL)
    // ============================================================

    @Test
    @DisplayName("✅ [PASS] Category: Lấy danh sách tất cả thể loại")
    public void testCategory_ItemsExist() {
        Response response = RestAssured.given().spec(requestSpec)
                .when().get("/v1/api/the-loai")
                .then().statusCode(200).body("status", equalTo("success")).extract().response();

        Object dataObj = response.jsonPath().get("data");
        List<?> list;
        if (dataObj instanceof List) {
            list = (List<?>) dataObj;
        } else {
            list = response.jsonPath().getList("data.items");
        }

        System.out.println("[PASS] Category: Lấy được " + (list != null ? list.size() : 0) + " thể loại ✓");
        assertThat("Danh sách thể loại không được rỗng", list, not(empty()));
    }

    @Test
    @DisplayName("✅ [PASS] Category: Lấy phim Hành động - Âu Mỹ")
    public void testCategory_HanhDong_AuMy() {
        RestAssured.given().spec(requestSpec)
                .queryParam("country", "au-my")
                .when().get("/v1/api/the-loai/hanh-dong")
                .then().statusCode(200)
                .body("status", equalTo("success"))
                .body("data.titlePage", containsStringIgnoringCase("Hành động"));
        System.out.println("[PASS] Category: Hành động - Âu Mỹ ✓");
    }

    @Test
    @DisplayName("✅ [PASS] Category: Lấy phim Tình cảm - Hàn Quốc")
    public void testCategory_TinhCam_HanQuoc() {
        RestAssured.given().spec(requestSpec)
                .queryParam("country", "han-quoc")
                .when().get("/v1/api/the-loai/tinh-cam")
                .then().statusCode(200)
                .body("data.titlePage", containsStringIgnoringCase("Tình cảm"));
        System.out.println("[PASS] Category: Tình cảm - Hàn Quốc ✓");
    }

    @Test
    @DisplayName("✅ [PASS] Category: Chi tiết thể loại (Đảm bảo trả về danh sách phim)")
    public void testCategory_Details() {
        Response response = RestAssured.given().spec(requestSpec)
                .when().get("/v1/api/the-loai/kinh-di")
                .then().statusCode(200)
                .body("status", equalTo("success"))
                .body("data.items", not(empty()))
                .extract().response();

        List<?> items = response.jsonPath().getList("data.items");
        System.out.println("[PASS] Category: Thể loại 'kinh-di' có " + items.size() + " phim ✓");
    }

    @Test
    @DisplayName("✅ [PASS] Category: Kiểm tra Limit=5")
    public void testCategory_Limit() {
        Response response = RestAssured.given().spec(requestSpec)
                .queryParam("limit", 5)
                .when().get("/v1/api/the-loai/hanh-dong")
                .then().statusCode(200).extract().response();
        List<?> items = response.jsonPath().getList("data.items");
        assertThat(items.size(), lessThanOrEqualTo(5));
        System.out.println("[PASS] Category: Limit 5 hoạt động (nhận " + items.size() + " phim) ✓");
    }

    @Test
    @DisplayName("✅ [PASS] Category: Kiểm tra Pagination Metadata")
    public void testCategory_Pagination() {
        RestAssured.given().spec(requestSpec)
                .when().get("/v1/api/the-loai/hanh-dong")
                .then().statusCode(200)
                .body("data.params.pagination.currentPage", notNullValue())
                .body("data.params.pagination.totalItems", greaterThanOrEqualTo(0));
        System.out.println("[PASS] Category: Pagination metadata hợp lệ ✓");
    }

    // @Test
    // @DisplayName("❌ [FAIL] Category: Cố tình sai titlePage")
    // public void testCategory_IntentionalFail() {
    // RestAssured.given().spec(requestSpec)
    // .when().get("/v1/api/the-loai/hanh-dong")
    // .then().statusCode(200)
    // .body("data.titlePage", equalTo("Phim Hoạt Hình Sai Lạc"));
    // }

    // ============================================================
    // 🌍 SECTION 2: QUỐC GIA (COUNTRY) - 5 TESTS
    // ============================================================

    @Test
    @DisplayName("✅ [PASS] Country: Lấy danh sách tất cả quốc gia")
    public void testCountry_ItemsExist() {
        Response response = RestAssured.given().spec(requestSpec)
                .when().get("/v1/api/quoc-gia")
                .then().statusCode(200).body("status", equalTo("success")).extract().response();

        // Thử lấy list từ data, nếu không được thử data.items
        Object dataObj = response.jsonPath().get("data");
        List<?> list;
        if (dataObj instanceof List) {
            list = (List<?>) dataObj;
        } else {
            list = response.jsonPath().getList("data.items");
        }

        System.out.println("[PASS] Country: Lấy được " + (list != null ? list.size() : 0) + " quốc gia ✓");
        assertThat(list, not(empty()));
    }

    @Test
    @DisplayName("✅ [PASS] Country: Lấy phim Hàn Quốc")
    public void testCountry_HanQuoc() {
        RestAssured.given().spec(requestSpec)
                .when().get("/v1/api/quoc-gia/han-quoc")
                .then().statusCode(200)
                .body("data.titlePage", containsStringIgnoringCase("Hàn Quốc"));
        System.out.println("[PASS] Country: Phim Hàn Quốc ✓");
    }

    @Test
    @DisplayName("✅ [PASS] Country: Lấy phim Trung Quốc - Cổ trang")
    public void testCountry_TrungQuoc_CoTrang() {
        RestAssured.given().spec(requestSpec)
                .queryParam("category", "co-trang")
                .when().get("/v1/api/quoc-gia/trung-quoc")
                .then().statusCode(200)
                .body("data.titlePage", containsStringIgnoringCase("Trung Quốc"));
        System.out.println("[PASS] Country: Trung Quốc - Cổ trang ✓");
    }

    @Test
    @DisplayName("✅ [PASS] Country: Kiểm tra Pagination trang 2")
    public void testCountry_Pagination_Page2() {
        RestAssured.given().spec(requestSpec)
                .queryParam("page", 2)
                .when().get("/v1/api/quoc-gia/han-quoc")
                .then().statusCode(200)
                .body("data.params.pagination.currentPage", equalTo(2));
        System.out.println("[PASS] Country: Chuyển trang 2 thành công ✓");
    }

    // @Test
    // @DisplayName("❌ [FAIL] Country: Kiểm tra sai status code (Mong đợi 404)")
    // public void testCountry_IntentionalFail_Status() {
    // RestAssured.given().spec(requestSpec)
    // .when().get("/v1/api/quoc-gia/han-quoc")
    // .then().statusCode(404); // Thực tế trả 200
    // }

    // ============================================================
    // 📅 SECTION 3: NĂM PHÁT HÀNH (YEAR) - 5 TESTS
    // ============================================================

    @Test
    @DisplayName("✅ [PASS] Year: Lấy danh sách tất cả các năm")
    public void testYear_ItemsExist() {
        Response response = RestAssured.given().spec(requestSpec)
                .when().get("/v1/api/nam-phat-hanh")
                .then().statusCode(200).extract().response();

        Object dataObj = response.jsonPath().get("data");
        List<?> list;
        if (dataObj instanceof List) {
            list = (List<?>) dataObj;
        } else {
            list = response.jsonPath().getList("data.items");
        }

        System.out.println("[PASS] Year: Lấy được " + (list != null ? list.size() : 0) + " năm phát hành ✓");
        assertThat(list, not(empty()));
    }

    @Test
    @DisplayName("✅ [PASS] Year: Lấy phim năm 2025")
    public void testYear_2025() {
        RestAssured.given().spec(requestSpec)
                .when().get("/v1/api/nam-phat-hanh/2025")
                .then().statusCode(200)
                .body("data.titlePage", containsStringIgnoringCase("2025"));
        System.out.println("[PASS] Year: Phim năm 2025 ✓");
    }

    @Test
    @DisplayName("✅ [PASS] Year: Lấy phim năm 2024 - Hành động")
    public void testYear_2024_HanhDong() {
        RestAssured.given().spec(requestSpec)
                .queryParam("category", "hanh-dong")
                .when().get("/v1/api/nam-phat-hanh/2024")
                .then().statusCode(200)
                .body("data.titlePage", containsStringIgnoringCase("2024"));
        System.out.println("[PASS] Year: Phim năm 2024 - Hành động ✓");
    }

    @Test
    @DisplayName("✅ [PASS] Year: Kiểm tra số lượng phim trang 1")
    public void testYear_ItemCount() {
        Response response = RestAssured.given().spec(requestSpec)
                .when().get("/v1/api/nam-phat-hanh/2025")
                .then().statusCode(200).extract().response();
        List<?> items = response.jsonPath().getList("data.items");
        System.out.println("[PASS] Year: Trang 1 năm 2025 có " + items.size() + " phim ✓");
        assertThat(items, notNullValue());
    }

    // @Test
    // @DisplayName("❌ [FAIL] Year: Kiểm tra sai trường status (Mong đợi 'error')")
    // public void testYear_IntentionalFail_StatusString() {
    // RestAssured.given().spec(requestSpec)
    // .when().get("/v1/api/nam-phat-hanh/2025")
    // .then().statusCode(200)
    // .body("status", equalTo("error")); // Thực tế trả "success"
    // }
}

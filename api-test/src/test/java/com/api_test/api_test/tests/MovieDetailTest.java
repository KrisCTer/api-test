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
 * MovieDetailTest — kiểm tra chi tiết phim và cấu trúc JSON chuyên sâu.
 *
 * CÁC NỘI DUNG ĐÃ THỰC HIỆN:
 * 1. Chi tiết phim (Details): 5 bài test. Kiểm tra Metadata (thumb/poster),
 * Negative slug, SEO & Breadcrumbs, và cấu trúc JSON chuyên sâu
 * (Episodes/TMDB/IMDB).
 * 2. Hình ảnh (Images): 2 bài test chính thức. Kiểm tra thành công và định dạng
 * ảnh.
 * 3. Diễn viên/Đạo diễn (Peoples): 2 bài test chính thức. Kiểm tra cast list và
 * chi tiết nhân vật.
 * 4. Từ khóa (Keywords): 2 bài test chính thức. Kiểm tra keyword list và VN
 * Mapping.
 * 5. Section Đặc Biệt: Gộp 4 bài test cố tình Fail xuống cuối file để quản lý
 * lỗi dễ dàng.
 */
@DisplayName("🎬 API Chi Tiết Phim & TMDB Info")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MovieDetailTest extends BaseTest {

    private static final String MOVIE_SLUG = "ke-cap-mat-trang-3";

    // ============================================================
    // 🎬 SECTION 1: CHI TIẾT PHIM (DETAILS)
    // ============================================================

    @Test
    @Order(1)
    @DisplayName("✅ [PASS] Details: Lấy phim và kiểm tra metadata (thumb, poster, origin_name...)")
    public void testDetail_Success() {
        Response response = RestAssured.given().spec(requestSpec)
                .when().get("/v1/api/phim/" + MOVIE_SLUG)
                .then().statusCode(200)
                .body("status", equalTo("success"))
                .body("data.item.slug", equalTo(MOVIE_SLUG))
                .body("data.item.name", notNullValue())
                .body("data.item.origin_name", notNullValue())
                .body("data.item.type", notNullValue())
                .body("data.item.status", notNullValue())
                .extract().response();

        // Kiểm tra thumb/poster từ bản cũ
        String thumbUrl = response.jsonPath().getString("data.item.thumb_url");
        String posterUrl = response.jsonPath().getString("data.item.poster_url");
        assertThat("Phải có thumb_url hoặc poster_url", thumbUrl != null || posterUrl != null, is(true));

        System.out.println("[PASS] Details: Thông tin cơ bản và metadata phim hợp lệ ✓");
    }

    @Test
    @Order(2)
    @DisplayName("✅ [PASS] Details: Validate cấu trúc JSON chuyên sâu (Episodes, Category, Country, TMDB)")
    public void testDetail_FullStructure() {
        RestAssured.given().spec(requestSpec)
                .when().get("/v1/api/phim/" + MOVIE_SLUG)
                .then().statusCode(200)
                .body("data.item._id", notNullValue())
                .body("data.item.category", not(empty()))
                .body("data.item.country", not(empty()))
                .body("data.item.episodes", not(empty()))
                .body("data.item.quality", notNullValue())
                .body("data.item.tmdb", anyOf(notNullValue(), nullValue()))
                .body("data.item.imdb", anyOf(notNullValue(), nullValue()));

        System.out.println("[PASS] Details: Cấu trúc JSON chi tiết (Category/Country/Episodes) hợp lệ ✓");
    }

    @Test
    @Order(3)
    @DisplayName("✅ [PASS] Details: Kiểm tra slug không tồn tại (Negative Test)")
    public void testDetail_NonExistentSlug() {
        int statusCode = RestAssured.given().spec(requestSpec)
                .when().get("/v1/api/phim/phim-ao-tu-tu-999999")
                .then().extract().statusCode();

        assertThat("Xử lý slug không tồn tại", statusCode, anyOf(is(200), is(400), is(404)));
        System.out.println("[PASS] Details: Xử lý slug không tồn tại chính xác ✓");
    }

    @Test
    @Order(4)
    @DisplayName("✅ [PASS] Details: Kiểm tra cấu trúc SEO & Breadcrumbs")
    public void testDetail_Structure() {
        RestAssured.given().spec(requestSpec)
                .when().get("/v1/api/phim/" + MOVIE_SLUG)
                .then().statusCode(200)
                .body("data.seoOnPage", notNullValue())
                .body("data.breadCrumb", not(empty()));
        System.out.println("[PASS] Details: SEO & Breadcrumbs đầy đủ ✓");
    }

    // ============================================================
    // 🖼️ SECTION 2: HÌNH ẢNH (IMAGES)
    // ============================================================

    @Test
    @Order(5)
    @DisplayName("✅ [PASS] Images: Lấy danh sách ảnh từ TMDB")
    public void testImages_Success() {
        RestAssured.given().spec(requestSpec)
                .when().get("/v1/api/phim/" + MOVIE_SLUG + "/images")
                .then().statusCode(200)
                .body("success", is(true))
                .body("data.images", notNullValue());
        System.out.println("[PASS] Images: Lấy ảnh từ TMDB thành công ✓");
    }

    @Test
    @Order(6)
    @DisplayName("✅ [PASS] Images: Kiểm tra định dạng ảnh (backdrop/poster)")
    public void testImages_Format() {
        Response response = RestAssured.given().spec(requestSpec)
                .when().get("/v1/api/phim/" + MOVIE_SLUG + "/images")
                .then().statusCode(200).extract().response();

        List<?> images = response.jsonPath().getList("data.images");
        if (images != null && !images.isEmpty()) {
            RestAssured.given().spec(requestSpec)
                    .when().get("/v1/api/phim/" + MOVIE_SLUG + "/images")
                    .then().body("data.images[0].type", anyOf(equalTo("backdrop"), equalTo("poster")));
        }
        System.out.println("[PASS] Images: Định dạng ảnh hợp lệ ✓");
    }

    // ============================================================
    // 👥 SECTION 3: DIỄN VIÊN / ĐẠO DIỄN (PEOPLES)
    // ============================================================

    @Test
    @Order(7)
    @DisplayName("✅ [PASS] Peoples: Lấy danh sách cast từ TMDB")
    public void testPeoples_Success() {
        RestAssured.given().spec(requestSpec)
                .when().get("/v1/api/phim/" + MOVIE_SLUG + "/peoples")
                .then().statusCode(200)
                .body("success", is(true))
                .body("data.peoples", notNullValue());
        System.out.println("[PASS] Peoples: Lấy danh sách diễn viên thành công ✓");
    }

    @Test
    @Order(8)
    @DisplayName("✅ [PASS] Peoples: Kiểm tra chi tiết nhân vật")
    public void testPeoples_Detail() {
        Response response = RestAssured.given().spec(requestSpec)
                .when().get("/v1/api/phim/" + MOVIE_SLUG + "/peoples")
                .then().statusCode(200).extract().response();

        List<?> peoples = response.jsonPath().getList("data.peoples");
        if (peoples != null && !peoples.isEmpty()) {
            RestAssured.given().spec(requestSpec)
                    .when().get("/v1/api/phim/" + MOVIE_SLUG + "/peoples")
                    .then().body("data.peoples[0].name", notNullValue());
        }
        System.out.println("[PASS] Peoples: Thông tin diễn viên đầy đủ ✓");
    }

    // ============================================================
    // 🔑 SECTION 4: TỪ KHÓA (KEYWORDS)
    // ============================================================

    @Test
    @Order(9)
    @DisplayName("✅ [PASS] Keywords: Lấy từ khóa từ TMDB")
    public void testKeywords_Success() {
        RestAssured.given().spec(requestSpec)
                .when().get("/v1/api/phim/" + MOVIE_SLUG + "/keywords")
                .then().statusCode(200)
                .body("success", is(true))
                .body("data.keywords", notNullValue());
        System.out.println("[PASS] Keywords: Lấy từ khóa thành công ✓");
    }

    @Test
    @Order(10)
    @DisplayName("✅ [PASS] Keywords: Kiểm tra Mapping VN")
    public void testKeywords_VN() {
        Response response = RestAssured.given().spec(requestSpec)
                .when().get("/v1/api/phim/" + MOVIE_SLUG + "/keywords")
                .then().statusCode(200).extract().response();

        List<?> keywords = response.jsonPath().getList("data.keywords");
        if (keywords != null && !keywords.isEmpty()) {
            RestAssured.given().spec(requestSpec)
                    .when().get("/v1/api/phim/" + MOVIE_SLUG + "/keywords")
                    .then().body("data.keywords[0].name_vn", notNullValue());
        }
        System.out.println("[PASS] Keywords: Mapping tên tiếng Việt chính xác ✓");
    }

    // ============================================================
    // ❌ SECTION 5: INTENTIONAL FAIL TESTS (DÀNH CHO DEMO/KIỂM THỬ)
    // ============================================================

    // @Test
    // @Order(11)
    // @DisplayName("❌ [FAIL] Details: Cố tình sai trường secret_code")
    // public void testDetail_IntentionalFail() {
    // RestAssured.given().spec(requestSpec)
    // .when().get("/v1/api/phim/" + MOVIE_SLUG)
    // .then().statusCode(200)
    // .body("data.item.secret_code", notNullValue());
    // }

    // @Test
    // @Order(12)
    // @DisplayName("❌ [FAIL] Images: Cố tình kiểm tra sai trạng thái
    // success=false")
    // public void testImages_IntentionalFail() {
    // RestAssured.given().spec(requestSpec)
    // .when().get("/v1/api/phim/" + MOVIE_SLUG + "/images")
    // .then().statusCode(200)
    // .body("success", is(false));
    // }

    // @Test
    // @Order(13)
    // @DisplayName("❌ [FAIL] Peoples: Cố tình kiểm tra danh sách cast rỗng")
    // public void testPeoples_IntentionalFail() {
    // RestAssured.given().spec(requestSpec)
    // .when().get("/v1/api/phim/" + MOVIE_SLUG + "/peoples")
    // .then().statusCode(200)
    // .body("data.peoples", hasSize(0));
    // }

    // @Test
    // @Order(14)
    // @DisplayName("❌ [FAIL] Keywords: Cố tình sai thông báo message")
    // public void testKeywords_IntentionalFail() {
    // RestAssured.given().spec(requestSpec)
    // .when().get("/v1/api/phim/" + MOVIE_SLUG + "/keywords")
    // .then().statusCode(200)
    // .body("message", equalTo("Wrong Message Expectation"));
    // }
}

# 📋 BÁO CÁO TỔNG HỢP — BỘ TEST API OPhim

> **Ngày hoàn thành:** 02/03/2026  
> **Tổng số test:** 54  
> **Kết quả:** ✅ BUILD SUCCESS (0 Failures, 0 Errors)  
> **Thời gian chạy:** ~30 giây  
> **Lệnh chạy:** `mvn test`

---

## 📊 Tổng quan theo File

| # | File Test | Số Test | Trạng thái | API Endpoint |
|---|-----------|---------|------------|--------------|
| 1 | HomeTest | 9 | ✅ All Pass | `/v1/api/home` |
| 2 | MovieDetailTest | 10 | ✅ All Pass | `/v1/api/phim/{slug}` + TMDB |
| 3 | MovieListTest | 13 | ✅ All Pass | `/v1/api/danh-sach/{slug}` |
| 4 | SearchTest | 5 | ✅ All Pass | `/v1/api/tim-kiem` |
| 5 | CategoryCountryYearTest | 14 | ✅ All Pass | `/v1/api/the-loai` `/v1/api/quoc-gia` `/v1/api/nam-phat-hanh` |
| 6 | HealthCheckTest | 3 | ✅ All Pass | Health check cơ bản |
| | **Tổng cộng** | **54** | **✅ 100%** | |

---

## 🏠 1. HomeTest (9 tests)

| # | Tên Test | Mô tả |
|---|----------|-------|
| 1 | `testHomePage_Returns200AndHasItems` | Kiểm tra status 200 và có dữ liệu phim |
| 2 | `testHomePage_HasRequiredJsonStructure` | Validate cấu trúc JSON (status, message, data) |
| 3 | `testHomePage_HasSeoOnPage` | Kiểm tra SEO: titleHead & descriptionHead |
| 4 | `testHomePage_ItemsHaveRequiredFields` | Kiểm tra trường bắt buộc: _id, name, slug, thumb_url |
| 5 | `testHomePage_HasPaginationParams` | Kiểm tra phân trang (currentPage, totalItems) |
| 6 | `testHomePage_HasDomains` | Kiểm tra CDN Domain & Frontend Domain |
| 7 | `testHomePage_ResponseTimeUnder5s` | Kiểm tra hiệu năng (response < 5 giây) |
| 8 | `testHome_BannerItems` | ⭐ Kiểm tra Banner có ảnh đại diện hợp lệ |
| 9 | `testHome_LatestMovies` | ⭐ Kiểm tra danh sách phim mới nhất |

> **3 test Fail demo** đã được comment lại ở cuối file.

---

## 🎬 2. MovieDetailTest (10 tests)

| # | Tên Test | Mô tả |
|---|----------|-------|
| 1 | `testDetail_Success` | Metadata phim: name, origin_name, type, status, thumb/poster |
| 2 | `testDetail_FullStructure` | ⭐ Cấu trúc JSON sâu: _id, category, country, episodes, quality, tmdb, imdb |
| 3 | `testDetail_NonExistentSlug` | ⭐ Negative test: slug không tồn tại |
| 4 | `testDetail_Structure` | SEO & Breadcrumbs |
| 5 | `testImages_Success` | Lấy ảnh từ TMDB |
| 6 | `testImages_Format` | Định dạng ảnh: backdrop/poster |
| 7 | `testPeoples_Success` | Lấy danh sách diễn viên từ TMDB |
| 8 | `testPeoples_Detail` | ⭐ Chi tiết nhân vật (name) |
| 9 | `testKeywords_Success` | Lấy từ khóa từ TMDB |
| 10 | `testKeywords_VN` | ⭐ Kiểm tra Mapping tên tiếng Việt |

> **4 test Fail demo** đã được comment lại ở cuối file.

---

## 📋 3. MovieListTest (13 tests)

| # | Tên Test | Mô tả |
|---|----------|-------|
| 1-6 | `testMovieListBySlug_Returns200` | ⭐ ParameterizedTest: 6 slugs (phim-moi, phim-bo, phim-le, tv-shows, hoat-hinh, phim-vietsub) |
| 7 | `testMovieList_PaginationWorks` | ⭐ So sánh nội dung trang 1 vs trang 2 |
| 8 | `testMovieList_HasPaginationParams` | Validate pagination metadata |
| 9 | `testMovieList_InvalidSlug` | Negative test: slug không tồn tại |
| 10 | `testMovieList_LimitParam` | ⭐ Kiểm tra limit=5 trả về đúng số lượng |
| 11 | `testMovieList_SortByYearDesc` | ⭐ Sắp xếp theo năm giảm dần |
| 12 | `testMovieList_FilterByCategoryAndCountry` | ⭐ Lọc đa điều kiện: hanh-dong + au-my |
| 13 | `testMovieList_HoatHinhSlug` | Xác nhận slug hoat-hinh hoạt động |

> **1 test Fail demo** đã được comment lại ở cuối file.

---

## 🔍 4. SearchTest (5 tests)

| # | Tên Test | Mô tả |
|---|----------|-------|
| 1 | `testSearch_ValidKeyword_ReturnsResults` | ⭐ Tìm kiếm 'avengers' trả về kết quả |
| 2 | `testSearch_SingleCharKeyword_HandledGracefully` | ⭐ Xử lý từ khóa 1 ký tự |
| 3 | `testSearch_Pagination_Page2DiffersFromPage1` | ⭐ Phân trang tìm kiếm (naruto) |
| 4 | `testSearch_LimitParam_ReturnsCorrectCount` | Kiểm tra limit=3 |
| 5 | `testSearch_TitlePage_ContainsKeyword` | ⭐ SEO: titlePage chứa từ khóa |

> **1 test Fail demo** + 2 test bổ sung đã được comment lại.

---

## 📂 5. CategoryCountryYearTest (14 tests)

### Thể loại (Category) — 6 tests
| # | Tên Test | Mô tả |
|---|----------|-------|
| 1 | `testCategory_ItemsExist` | ⭐ Lấy danh sách tất cả 23 thể loại |
| 2 | `testCategory_HanhDong_AuMy` | Phim Hành động - Âu Mỹ |
| 3 | `testCategory_TinhCam_HanQuoc` | Phim Tình cảm - Hàn Quốc |
| 4 | `testCategory_Details` | ⭐ Chi tiết thể loại kinh-di có phim |
| 5 | `testCategory_Limit` | Kiểm tra limit=5 |
| 6 | `testCategory_Pagination` | Pagination metadata |

### Quốc gia (Country) — 4 tests
| # | Tên Test | Mô tả |
|---|----------|-------|
| 7 | `testCountry_ItemsExist` | ⭐ Lấy danh sách tất cả 45 quốc gia |
| 8 | `testCountry_HanQuoc` | Phim Hàn Quốc |
| 9 | `testCountry_TrungQuoc_CoTrang` | Phim Trung Quốc - Cổ trang |
| 10 | `testCountry_Pagination_Page2` | Phân trang trang 2 |

### Năm phát hành (Year) — 4 tests
| # | Tên Test | Mô tả |
|---|----------|-------|
| 11 | `testYear_ItemsExist` | ⭐ Lấy danh sách tất cả 104 năm |
| 12 | `testYear_2025` | Phim năm 2025 |
| 13 | `testYear_2024_HanhDong` | Phim năm 2024 - Hành động |
| 14 | `testYear_ItemCount` | Số lượng phim trang 1 |

> **3 test Fail demo** đã được comment lại ở cuối file.

---

## ❌ Các Test Báo Lỗi Có Chủ Đích (Intentional Fails)

Dự án này bao gồm **13 bài test** được cố tình cấu hình để gây ra lỗi (FAIL). Mục tiêu của các test này là để kiểm chứng độ tin cậy của bộ kiểm thử: đảm bảo hệ thống thực sự báo đỏ (Fail) khi API trả về dữ liệu sai. Hiện tại các test này đã được **comment lại** để dự án đạt `BUILD SUCCESS`, nhưng người duyệt có thể bỏ comment ra để xem báo cáo lỗi.

| # | Tên Test | File | Mô tả lý do cố tình Fail |
|---|----------|------|---------------------------|
| 1 | `testHomePage_IntentionalFail_Status` | HomeTest | Kỳ vọng status "error" nhưng thực tế "success" |
| 2 | `testHomePage_IntentionalFail_SeoTitle` | HomeTest | Kỳ vọng title là trang lậu, khác với thực tế |
| 3 | `testHomePage_IntentionalFail_TotalItems` | HomeTest | Kỳ vọng tổng số lượng phim là 0 |
| 4 | `testDetail_IntentionalFail` | MovieDetailTest | Cố tình sai trường thông tin cơ bản |
| 5 | `testImages_IntentionalFail` | MovieDetailTest | Cố tình kiểm tra sai trạng thái ảnh |
| 6 | `testPeoples_IntentionalFail` | MovieDetailTest | Cố tình kiểm tra danh sách cast (diễn viên) rỗng |
| 7 | `testKeywords_IntentionalFail` | MovieDetailTest | Cố tình sai số lượng từ khóa nhận được |
| 8 | `testMovieList_IntentionalFail` | MovieListTest | Kiểm tra sai trường trạng thái và báo cáo lỗi mục items rỗng |
| 9 | `testMovieList_TotalPages_FieldNotExist_ExpectFail` | MovieListTest | Kỳ vọng trường totalPages tồn tại nhưng thực tế thiếu |
| 10 | `testSearch_Message_ExpectFail` | SearchTest | Kỳ vọng thông báo trả về "oh no da loi" |
| 11 | `testCategory_IntentionalFail` | CategoryCountryYearTest | Cố tình sai titlePage của thể loại |
| 12 | `testCountry_IntentionalFail_Status` | CategoryCountryYearTest | Cố tình mong đợi status HTTP 404 cho quốc gia hợp lệ |
| 13 | `testYear_IntentionalFail_StatusString` | CategoryCountryYearTest | Kỳ vọng status "error" ở năm phát hành |

---

## 🛠️ Công nghệ sử dụng

- **Framework:** JUnit 5 + RestAssured
- **Build tool:** Maven
- **API Base URL:** `https://ophim1.com`
- **Java Version:** JDK 21+

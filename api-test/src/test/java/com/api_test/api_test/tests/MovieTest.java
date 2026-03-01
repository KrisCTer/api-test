package com.api_test.api_test.tests;

import com.api_test.api_test.base.BaseTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

public class MovieTest extends BaseTest {
    @Test
    public void testGetMovies() {
        ((ValidatableResponse)((ValidatableResponse)((Response)RestAssured.given().header("User-Agent", "Mozilla/5.0", new Object[0]).when().get("/v1/api/phim/loi-nhan-cuoi-cung-phan-2/peoples", new Object[0])).then()).statusCode(200)).body("data.size()", Matchers.greaterThan(0), new Object[0]);
    }
}
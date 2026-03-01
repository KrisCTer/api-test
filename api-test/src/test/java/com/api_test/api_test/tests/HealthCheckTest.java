package com.api_test.api_test.tests;

import com.api_test.api_test.base.BaseTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.Test;

public class HealthCheckTest extends BaseTest {
    @Test
    public void testHomePageAccessible() {
        ((ValidatableResponse)((Response)RestAssured.given().header("User-Agent", "Mozilla/5.0", new Object[0]).when().get("/", new Object[0])).then()).statusCode(200);
    }
}
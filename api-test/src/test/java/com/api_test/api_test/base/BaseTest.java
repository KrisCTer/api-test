package com.api_test.api_test.base;

import com.api_test.api_test.utils.ConfigLoader;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;

public class BaseTest {
    @BeforeAll
    public static void setup() {
        String baseUrl = ConfigLoader.get("base.url");
        if (baseUrl == null) {
            throw new RuntimeException("base.url đang NULL trong config.properties");
        } else {
            RestAssured.baseURI = baseUrl;
        }
    }
}

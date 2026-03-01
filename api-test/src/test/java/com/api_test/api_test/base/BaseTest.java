package com.api_test.api_test.base;

import com.api_test.api_test.utils.ConfigLoader;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;

/**
 * BaseTest — cấu hình chung cho toàn bộ test suite.
 * Mọi test class đều extends class này.
 */
public class BaseTest {

    protected static RequestSpecification requestSpec;

    @BeforeAll
    public static void setup() {
        String baseUrl = ConfigLoader.get("base.url");
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new RuntimeException("base.url đang NULL hoặc rỗng trong config.properties");
        }

        RestAssured.baseURI = baseUrl;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        requestSpec = new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setContentType(ContentType.JSON)
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) RestAssured/5.4.0")
                .addHeader("Accept", "application/json")
                .log(LogDetail.METHOD)
                .log(LogDetail.URI)
                .build();

        System.out.println("=================================================");
        System.out.println("  OPhim API Test Suite — Base URL: " + baseUrl);
        System.out.println("=================================================");
    }
}

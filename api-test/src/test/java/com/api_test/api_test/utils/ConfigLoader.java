package com.api_test.api_test.utils;

import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static final Properties properties = new Properties();

    public static String get(String key) {
        return properties.getProperty(key);
    }

    static {
        try {
            try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream("config.properties")) {
                if (input == null) {
                    throw new RuntimeException("Không tìm thấy file config.properties");
                }

                properties.load(input);
            }

        } catch (Exception e) {
            throw new RuntimeException("Lỗi load config: " + e.getMessage());
        }
    }
}

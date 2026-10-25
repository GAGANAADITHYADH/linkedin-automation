package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

    private static final Properties props = new Properties();

    static {
        try (FileInputStream in = new FileInputStream("config.properties")) {
            props.load(in);
        } catch (IOException e) {
            // config.properties missing: fall back to environment variables
        }
    }

    private static String get(String key, String envName) {
        String env = System.getenv(envName);
        if (env != null && !env.isBlank()) {
            return env.trim();
        }
        return props.getProperty(key, "").trim();
    }

    public static String username() {
        return get("linkedin.username", "LINKEDIN_USERNAME");
    }

    public static String password() {
        return get("linkedin.password", "LINKEDIN_PASSWORD");
    }
}
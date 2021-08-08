package com.example.smell_generation_demo.util;

import java.io.IOException;
import java.util.Properties;

public class ConfigUtil {
    private static ConfigUtil instance;
    private Properties properties;

    private ConfigUtil() {
        properties = new Properties();
        try {
            properties.load(ConfigUtil.class.getClassLoader().getResourceAsStream("config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ConfigUtil getInstance() {
        if (instance == null)
           instance = new ConfigUtil();
        return instance;
    }

    public String getDumpingDirPath() {
        return properties.getProperty("path.final");
    }

    public int getMaxIterations() {
        return Integer.parseInt(properties.getProperty("iterations.max"));
    }
}

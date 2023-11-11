package com.orengolan.cheaptrips.config;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;

public class ConfigLoader {
    public static JSONObject loadConfig() {
        JSONParser parser = new JSONParser();
        try {
            FileReader reader = new FileReader("config.json");
            return (JSONObject) parser.parse(reader);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

package com.orengolan.CheapTrips.util;

import com.mongodb.lang.Nullable;
import com.orengolan.CheapTrips.news.NewsService;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

@Component
public class API {

    private final Logger logger = Logger.getLogger(NewsService.class.getName());

    public String buildAndExecuteRequest(String url, @Nullable Map<String, String> headers)  {
        logger.info("API>>buildAndExecuteRequest: Start method.");
        OkHttpClient client = new OkHttpClient();

        Request.Builder requestBuilder = new Request.Builder().url(url);

        // Add headers if provided
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }

        Request request = requestBuilder.build();

        logger.info("API>>buildAndExecuteRequest: Execute url: "+url);
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            if (response.body() != null) {
                logger.info("API>>buildAndExecuteRequest: Succeed fetch data.");
                return response.body().string();
            } else {
                throw new IOException("API>>buildAndExecuteRequest: Body of response if empty. " + response);
            }
        } catch(IOException e) {
            logger.severe("API>>buildAndExecuteRequest: Error get data: " + e.getMessage());
            return "API>>buildAndExecuteRequest: Error get data: " + e.getMessage();
        }
    }



}

package com.orengolan.cheaptrips.util;

import com.mongodb.lang.Nullable;
import com.orengolan.cheaptrips.news.NewsService;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

/**
 * The {@code API} class provides utility methods for building and executing HTTP requests to external APIs.
 * It uses the OkHttp library to handle HTTP communication and allows customization of request headers.
 *
 * Key Features:
 * - Builds and executes HTTP requests to external APIs.
 * - Supports customization of request headers through a {@code Map<String, String>}.
 * - Logs important events and actions using {@link java.util.logging.Logger}.
 *
 * Example Usage:
 * This class is used by other components, such as the {@link NewsService}, to interact with external APIs.
 * It encapsulates the logic for constructing HTTP requests, including handling headers, and executing them.
 *
 * Note: Exception handling ensures that unexpected situations, such as unsuccessful HTTP responses,
 * are properly logged and handled.
 */
@Component
public class API {

    private final Logger logger = Logger.getLogger(NewsService.class.getName());

    /**
     * Builds and executes an HTTP request to the specified URL with optional custom headers.
     *
     * @param url     The URL to send the HTTP request to.
     * @param headers A map of custom headers to include in the request. Can be {@code null} for no additional headers.
     * @return The response body as a string if the request is successful (HTTP status code 200).
     * @throws IOException If there are issues with the HTTP request or response.
     */
    public String buildAndExecuteRequest(String url, @Nullable Map<String, String> headers) throws IOException {
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
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        if (response.body() != null && response.code()==200) {
            logger.info("API>>buildAndExecuteRequest: Succeed fetch data.");
            return response.body().string();
        } else {
            throw new IOException("API>>buildAndExecuteRequest: Body of response if empty. " + response);
        }
    }



}

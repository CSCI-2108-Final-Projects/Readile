package com.readile.readile.utils;

import org.json.JSONObject;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class ImageAPIConnector {

    private static final String UNSPLASH_API_BASE_URL = "https://api.unsplash.com";
    private static final String UNSPLASH_API_KEY = "0GwLtEzCRzqceCP3YcWUglUFo_54e935DuGCkrODGp8";

    private static final WebClient webClient = WebClient.builder()
            .exchangeStrategies(ExchangeStrategies.builder()
                    .codecs(configurer -> configurer
                            .defaultCodecs()
                            .maxInMemorySize(32 * 1024 * 1024))
                    .build())
            .baseUrl(UNSPLASH_API_BASE_URL.concat("/photos/random/")).build();

    private ImageAPIConnector() {
    }

    public static String getRandomImage(String query) {
        String jsonResult = webClient.get()
                .uri("?query={query}", query)
                .header("Authorization", "Client-ID ".concat(UNSPLASH_API_KEY))
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> Mono.just("Not Found"))
                .block();

        if(jsonResult.equals("Not Found"))
            return "default-category-image.jpg";

        return parseImageJSON(jsonResult);
    }

    public static String parseImageJSON(String json) {
        JSONObject imageUrlsJsonObject = new JSONObject(json).getJSONObject("urls");
        return imageUrlsJsonObject.getString("small");
    }
}
package ru.practicum.shareit.base;

import org.springframework.web.reactive.function.client.WebClient;


public class BaseWebClient {


    protected final WebClient webClient;

    public BaseWebClient(final String baseUrl, final String apiPrefix) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl + apiPrefix)
                .build();
    }
    public BaseWebClient(final WebClient webClient) {
        this.webClient = webClient;
    }
}
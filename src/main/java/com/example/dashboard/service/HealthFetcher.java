package com.example.dashboard.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class HealthFetcher {

    private final WebClient webClient;

    public HealthFetcher(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    public Mono<Map<String, Object>> fetch(String name, String url) {
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(Map.class)
                .map(body -> Map.of(
                        "name", name,
                        "status", body.getOrDefault("status", "UNKNOWN"),
                        "details", body.get("details")
                ))
                .onErrorResume(e -> Mono.just(Map.of(
                        "name", name,
                        "status", "DOWN",
                        "error", e.getMessage()
                )));
    }

    public Flux<Map<String, Object>> fetchAll(List<Map<String, String>> services) {
        return Flux.fromIterable(services)
                   .flatMap(svc -> fetch(svc.get("name"), svc.get("url")));
    }
}

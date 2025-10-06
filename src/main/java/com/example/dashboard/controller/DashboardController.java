package com.example.dashboard.controller;

import com.example.dashboard.service.HealthFetcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Controller
public class DashboardController {

    private final HealthFetcher healthFetcher;
    private final List<Map<String, String>> services;

    public DashboardController(
            HealthFetcher healthFetcher,
            @Value("#{${dashboard.services}}") List<Map<String, String>> services) {
        this.healthFetcher = healthFetcher;
        this.services = services;
    }

    @org.springframework.web.bind.annotation.GetMapping("/dashboard")
    public Mono<Rendering> dashboard() {
        return healthFetcher.fetchAll(services)
                .collectList()
                .map(results -> Rendering.view("dashboard")
                        .modelAttribute("services", results)
                        .build());
    }
}

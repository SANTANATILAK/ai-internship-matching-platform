package com.tilak.internship_platform.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {

        HttpClient httpClient =
                HttpClient.create()
                        .responseTimeout(
                                Duration.ofSeconds(30)
                        )
                        .option(
                                io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS,
                                10000
                        );

        return WebClient.builder()
                .clientConnector(
                        new ReactorClientHttpConnector(
                                httpClient
                        )
                )
                .build();
    }
}
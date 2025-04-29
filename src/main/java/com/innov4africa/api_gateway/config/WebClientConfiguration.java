package com.innov4africa.api_gateway.config;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;

@Configuration
public class WebClientConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(WebClientConfiguration.class);

    @Bean
    public WebClient.Builder webClientBuilder() throws SSLException {
        // Configuration SSL pour ignorer les certificats
        SslContext sslContext = SslContextBuilder
            .forClient()
            .trustManager(InsecureTrustManagerFactory.INSTANCE)
            .build();

        // Configuration du client HTTP avec SSL
        HttpClient httpClient = HttpClient.create()
            .secure(t -> t.sslContext(sslContext))
            .wiretap(true);  // Active le logging des requêtes/réponses

        // Construction du WebClient avec logging
        return WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .filter(logRequest())
            .filter(logResponse());
    }

    // Filtre pour logger les requêtes
    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            logger.info("Request: {} {}", clientRequest.method(), clientRequest.url());
            clientRequest.headers().forEach((name, values) -> 
                values.forEach(value -> logger.info("{}={}", name, value))
            );
            return Mono.just(clientRequest);
        });
    }

    // Filtre pour logger les réponses
    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            logger.info("Response status: {}", clientResponse.statusCode());
            return Mono.just(clientResponse);
        });
    }
}


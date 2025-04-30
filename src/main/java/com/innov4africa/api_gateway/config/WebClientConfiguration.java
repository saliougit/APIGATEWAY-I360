package com.innov4africa.api_gateway.config;

import java.time.Duration;

import javax.net.ssl.SSLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

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

        // Configuration du client HTTP avec SSL et timeouts
        HttpClient httpClient = HttpClient.create()
            .secure(t -> t.sslContext(sslContext))
            .wiretap(true)  // Active le logging des requêtes/réponses
            .responseTimeout(Duration.ofSeconds(30))
            .option(io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);

        // Construction du WebClient avec logging détaillé
        return WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .filter(logRequest())
            .filter(logResponse());
    }

    // Filtre pour logger les requêtes avec plus de détails
    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            logger.info("Request: {} {} (Headers: {})", 
                clientRequest.method(), 
                clientRequest.url(),
                clientRequest.headers().toSingleValueMap());
            return Mono.just(clientRequest);
        });
    }

    // Filtre pour logger les réponses avec plus de détails
    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            logger.info("Response status: {} (Headers: {})", 
                clientResponse.statusCode(),
                clientResponse.headers().asHttpHeaders().toSingleValueMap());
            return Mono.just(clientResponse);
        });
    }
}


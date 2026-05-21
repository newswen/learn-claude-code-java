package com.yw.learnclaudecode.config;

import com.openai.client.OpenAIClient;
import com.openai.client.OpenAIClientAsync;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.client.okhttp.OpenAIOkHttpClientAsync;
import com.openai.core.LogLevel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAIClientConfig {

    @Value("${ai.openai.api-key}")
    private String apiKey;

    @Value("${ai.openai.base-url}")
    private String baseUrl;

    @Bean
    public OpenAIClient openAIClient() {
        return OpenAIOkHttpClient.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .build();
    }

    @Bean
    public OpenAIClientAsync openAIClientAsync() {
        return OpenAIOkHttpClientAsync
                .builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .build();
    }
}

package com.ggukgguk.batch.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ggukgguk.batch.extractKeyword.service.ColorService;
import com.ggukgguk.batch.extractKeyword.service.ColorServiceImpl;
import com.ggukgguk.batch.extractKeyword.service.ComprehendService;
import com.ggukgguk.batch.extractKeyword.service.ComprehendServiceImpl;
import com.ggukgguk.batch.checkContent.service.RekognizeService;
import com.ggukgguk.batch.checkContent.service.RekognizeServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BeansConfig {
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

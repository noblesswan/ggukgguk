package com.ggukgguk.batch.extractKeyword.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class ColorServiceImpl implements ColorService {
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    @Value("${openai.apikey}")
    private String OPEN_AI_API_KEY;

    @Override
    public List<String> getColor(String keyword) throws JsonProcessingException {
        String url = "https://api.openai.com/v1/completions";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("Authorization", "Bearer " + OPEN_AI_API_KEY);

        String body = "{\n" +
                "    \"model\": \"text-davinci-003\",\n" +
                "    \"prompt\": \"A hex color code array related to '" + keyword + "'; Format: [\\\"#FFFFFF\\\"]; Length: 3.\",\n" +
                "    \"max_tokens\": 40,\n" +
                "    \"temperature\": 0\n" +
                "  }";
        System.out.println(body);

        HttpEntity<?> requesMessage = new HttpEntity<>(body, httpHeaders);

        HttpEntity<String> response = restTemplate.postForEntity(url, requesMessage, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);

        HashMap<String, Object> dto = objectMapper.readValue(response.getBody(), HashMap.class);
        List<Map> choices = (List<Map>)dto.get("choices");

        String colorListRaw = (String)choices.get(0).get("text");

        List<String> colorList = objectMapper.readValue(colorListRaw, List.class);
        log.debug("Colors from AI: " + colorList);

        return colorList;
    }
}

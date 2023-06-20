package com.ggukgguk.batch.extractKeyword.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ggukgguk.batch.extractKeyword.vo.Keyword;
import com.ggukgguk.batch.extractKeyword.vo.Record;
import com.ggukgguk.batch.extractKeyword.vo.RecordKeyword;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class GgukggukNlpServiceImpl implements GgukggukNlpService {
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    @Value("${nlp.base-url}")
    private String BASE_URL;

    @Value("${nlp.num-of-keywords}")
    private String NUM_OF_KEYWORDS;

    @Override
    public List<RecordKeyword> extractKeywordFromRecord(Record record) {
        List<RecordKeyword> result = new ArrayList<RecordKeyword>();

        String input = record.getRecordComment();
        
        input = input.replace("\n", " "); // 개행시 400 오류 발생
        input = input.replace("\r", " ");
        input = input.replaceAll("[\\{\\}\\[\\]\\/?,;:|\\)*~`!^\\-_+<>@\\#$%&\\\\\\=\\(\\'\\\"]", " ");
        
        if (input == null || input.equals("")) {
            result.add(RecordKeyword.builder()
                    .recordId(record.getRecordId())
                    .recordKeyword("")
                    .build());
            return result;
        }

        try {
            sslTrustAllCerts();
            String url = BASE_URL + "/nlp/keyword";

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            String body = "{\n" +
                    "\t\"text\": \"" + input + "\",\n" +
                    "\t\"num\": " + NUM_OF_KEYWORDS + "\n" +
                    "}";

            HttpEntity<?> requesMessage = new HttpEntity<>(body, httpHeaders);

            HttpEntity<String> response = restTemplate.postForEntity(url, requesMessage, String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);

            HashMap<String, Object> dto = objectMapper.readValue(response.getBody(), HashMap.class);
            List<Map<String, Object>> keywords = (List<Map<String, Object>>)dto.get("data");
            for (Map<String, Object> keyword : keywords) {
                log.debug("추출 키워드: " + keyword.get("word"));
                log.debug("점수: " + keyword.get("score") + "\n");
                result.add(RecordKeyword.builder()
                        .recordId(record.getRecordId())
                        .recordKeyword((String)keyword.get("word"))
                        .build());
            }

        } catch (Exception e) {
            log.info("키워드 추출 요청중 예외가 발생했습니다.");
            log.info("요청: " + record);
            e.printStackTrace();
        }

        return result;
    }

    /**
     * SSL 인증서 검증 회피 (개발 서버용)
     */
    public void sslTrustAllCerts(){
        TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                    public void checkServerTrusted(X509Certificate[] certs, String authType) { }
                }
        };

        SSLContext sc;

        try {
            sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}

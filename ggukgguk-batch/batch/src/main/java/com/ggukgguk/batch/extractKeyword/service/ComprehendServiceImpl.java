package com.ggukgguk.batch.extractKeyword.service;

import com.ggukgguk.batch.extractKeyword.vo.Record;
import com.ggukgguk.batch.extractKeyword.vo.RecordKeyword;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.comprehend.ComprehendClient;
import software.amazon.awssdk.services.comprehend.model.ComprehendException;
import software.amazon.awssdk.services.comprehend.model.DetectKeyPhrasesRequest;
import software.amazon.awssdk.services.comprehend.model.DetectKeyPhrasesResponse;
import software.amazon.awssdk.services.comprehend.model.KeyPhrase;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ComprehendServiceImpl implements ComprehendService {
    @Override
    public List<RecordKeyword> extractKeywordFromRecord(Record record) {
        List<RecordKeyword> result = new ArrayList<RecordKeyword>();

        String input = record.getRecordComment();

        if (input == null || input.equals("")) {
            result.add(RecordKeyword.builder()
                    .recordId(record.getRecordId())
                    .recordKeyword("")
                    .build());
            return result;
        }

        try {
            DetectKeyPhrasesRequest detectKeyPhrasesRequest = DetectKeyPhrasesRequest.builder()
                    .text(input)
                    .languageCode("ko")
                    .build();

            Region region = Region.US_WEST_2;
            ComprehendClient comClient = ComprehendClient.builder()
                    .region(region)
                    .build();

            DetectKeyPhrasesResponse detectKeyPhrasesResult = comClient.detectKeyPhrases(detectKeyPhrasesRequest);
            List<KeyPhrase> phraseList = detectKeyPhrasesResult.keyPhrases();
            for (KeyPhrase keyPhrase : phraseList) {
                System.out.println(record.getRecordId());
                System.out.println(keyPhrase.text());
                System.out.println(keyPhrase.score());
                result.add(RecordKeyword.builder()
                        .recordId(record.getRecordId())
                        .recordKeyword(keyPhrase.text())
                        .build());
                log.debug("현재 리스트 내용: " + result.toString());
            }

        } catch (ComprehendException e) {
            log.info("AWS Comprehend API 요청중 예외가 발생했습니다.");
            log.info(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        return result;
    }
}

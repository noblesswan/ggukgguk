package com.ggukgguk.batch.extractKeyword.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ggukgguk.batch.extractKeyword.vo.Keyword;
import com.ggukgguk.batch.extractKeyword.vo.Record;
import com.ggukgguk.batch.extractKeyword.vo.RecordKeyword;

import java.util.List;

public interface GgukggukNlpService {
    public List<RecordKeyword> extractKeywordFromRecord(Record record);
}

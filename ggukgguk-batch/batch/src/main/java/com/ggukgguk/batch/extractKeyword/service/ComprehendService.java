package com.ggukgguk.batch.extractKeyword.service;

import com.ggukgguk.batch.extractKeyword.vo.Record;
import com.ggukgguk.batch.extractKeyword.vo.RecordKeyword;

import java.util.List;

public interface ComprehendService {
    public List<RecordKeyword> extractKeywordFromRecord(Record record);
}

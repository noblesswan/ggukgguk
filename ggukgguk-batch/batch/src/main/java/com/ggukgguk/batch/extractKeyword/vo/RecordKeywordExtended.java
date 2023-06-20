package com.ggukgguk.batch.extractKeyword.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordKeywordExtended {
    private int diaryId;
    private String memberId;
    private String diaryYear;
    private String diaryMonth;
    private int recordKeywordId;
    private int recordId;
    private String recordKeyword;
}
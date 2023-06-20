package com.ggukgguk.batch.extractKeyword.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecordKeyword {
    private int recordKeywordId;
    private int recordId;
    private String recordKeyword;
}
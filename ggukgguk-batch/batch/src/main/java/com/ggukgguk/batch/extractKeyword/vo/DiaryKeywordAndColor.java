package com.ggukgguk.batch.extractKeyword.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiaryKeywordAndColor {
    int diaryKeywordId;
    int diaryId;
    String diaryKeyword;
    int diaryFreq;
    String diaryColor;
    String memberId;
}

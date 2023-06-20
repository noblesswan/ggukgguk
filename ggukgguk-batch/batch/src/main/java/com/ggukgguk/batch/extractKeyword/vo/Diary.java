package com.ggukgguk.batch.extractKeyword.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Diary {
    private int diaryId;
    private String memberId;
    private String diaryYear;
    private String diaryMonth;
    private String mainColor;
    private String mainKeyword;
}

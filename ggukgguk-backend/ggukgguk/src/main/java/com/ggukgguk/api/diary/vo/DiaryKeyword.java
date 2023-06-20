package com.ggukgguk.api.diary.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiaryKeyword {

	private int diaryKeywordId;
	private int diaryId;
	private String diaryKeyword;
	private int diaryFreq;
}

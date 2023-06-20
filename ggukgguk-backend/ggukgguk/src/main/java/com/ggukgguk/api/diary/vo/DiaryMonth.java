package com.ggukgguk.api.diary.vo;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiaryMonth {

	private int diaryId;
	private String memberId;
	private String diaryYear;
	private String diaryMonth;
	private String mainColor;
	private String mainKeyword;
	private List<DiaryKeyword> diaryKeywordList;
	private List<DiaryRecord> diaryRecordList;
}

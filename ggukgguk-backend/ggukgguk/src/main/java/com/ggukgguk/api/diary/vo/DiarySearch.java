package com.ggukgguk.api.diary.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiarySearch {

	private String memberId;
	private String diaryYear;
	private String diaryMonth;
}

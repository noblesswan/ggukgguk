package com.ggukgguk.api.diary.vo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiaryRecord {

	private int recordDay;
	private int recordCount;
}

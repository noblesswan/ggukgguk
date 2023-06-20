package com.ggukgguk.api.record.vo;

import java.util.Date;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordSearch {
	
	private String memberId;
	private Date startDate;
	private String keyword;
	private String friendId;
}

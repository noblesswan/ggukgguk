package com.ggukgguk.api.admin.vo;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

//SELECT record_id, member_id, record_created_at, record_comment,
//media_file_blocked, media_file_checked
//FROM record r
//LEFT JOIN media_file m
//ON r.media_file_id = m.media_file_id
//ORDER BY record_created_at DESC

public class Content {
	private int totalRecord;
	private int recordId;
	private String memberId;	
	@JsonFormat(pattern = "yyyy-MM-dd'T'hh:mm:ss.SSSZ", timezone = "Asia/Seoul")
	private Date recordCreatedAt;
	private String mediaFileId;
	private String mediaTypeId;
	private int mediaFileBlocked; // block된 파일 확인 (0=non-blocked, 1=blocked)
	private int mediaFileChecked; // 미디어 업로드 유무 
}

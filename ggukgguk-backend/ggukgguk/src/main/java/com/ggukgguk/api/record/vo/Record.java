package com.ggukgguk.api.record.vo;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Record {

	private int recordId;
	private String memberId;
	private String memberNickname;
	private String friendNickname;
	private String recordComment;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "Asia/Seoul")
	private Date recordCreatedAt;
	private String mediaFileId;
	private String mediaTypeId;
	private double recordLocationY;
	private double recordLocationX;
	private boolean recordIsOpen;
	private String recordShareTo;
	private boolean recordShareAccepted;
	private String mainColor;
	private boolean mediaFileBlocked;
	private List<ReplyNickname> replyList;
}

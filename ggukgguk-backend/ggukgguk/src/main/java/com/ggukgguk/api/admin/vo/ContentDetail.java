package com.ggukgguk.api.admin.vo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContentDetail {
	private int recordId;
	private String memberId;
	private String recordComment;
	private Date recordCreatedAt;
	private String recordMediaFileId;
	private boolean recordIsOpen;
	private String recordShareTo;

	private int replyId;
	private String replyContent;
	private Date replyDate;
	
//	SELECT record.record_id, member.member_id, record.record_comment, record.record_created_at, 
//  	   record.media_file_id, record.record_is_open, record.record_share_to, 
//    	   reply.reply_id, reply.member_id AS reply_member_id, reply.reply_content, reply.reply_date
//	FROM record
//	LEFT JOIN reply ON record.record_id = reply.record_id
//	LEFT JOIN member ON reply.member_id = member.member_id
//	ORDER BY record.record_created_at DESC, reply.reply_date;

}

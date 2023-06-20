package com.ggukgguk.api.admin.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Member {

//	SELECT member_id, member_name, member_email, member_phone, member_birth
//	FROM member
	
	private String memberId;
	private String memberName;
	private String memberEmail;
	private String memberPhone;
	@JsonFormat(pattern = "yyyy-MM-dd'T'hh:mm:ss.SSSZ", timezone = "Asia/Seoul")
	private Date memberBirth; 
}

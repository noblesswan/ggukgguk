package com.ggukgguk.api.notification.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
	
	private int notificationId;
	private String notificationTypeId;
	@JsonFormat(pattern = "yyyy-MM-dd'T'hh:mm:ss.SSSZ", timezone = "Asia/Seoul")
	private Date notificationCreatedAt;
	private int referenceId;
	private String receiverId;
	private int notificationIsRead;
	private String notificationMessage;
}

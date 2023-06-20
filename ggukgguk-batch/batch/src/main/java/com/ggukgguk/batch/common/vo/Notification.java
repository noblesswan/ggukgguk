package com.ggukgguk.batch.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    private int notificationId;
    private String notificationTypeId;
    private Date notificationCreatedAt;
    private int referenceId;
    private String receiverId;
    private Boolean notificationIsRead;
    private String notificationMessage;
}
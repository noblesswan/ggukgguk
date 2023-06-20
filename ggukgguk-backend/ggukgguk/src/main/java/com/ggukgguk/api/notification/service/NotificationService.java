package com.ggukgguk.api.notification.service;

import java.util.List;

import com.ggukgguk.api.notification.vo.Notification;

public interface NotificationService {


	List<Notification> ListNotification(String receiverId);

	boolean createNotification(Notification noiNotification, String receiverId, String notficationId, int referenceId);

	boolean deleteNotify(int notificationId);

	int fetchUnreadNotiCount(String receiverId);

	boolean setReadNotify(Notification notification);

	Notification getOriginalNoti(int notificationId);

}

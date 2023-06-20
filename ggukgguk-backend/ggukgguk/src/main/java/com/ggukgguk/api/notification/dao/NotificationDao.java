package com.ggukgguk.api.notification.dao;

import java.util.List;

import com.ggukgguk.api.notification.vo.Notification;

public interface NotificationDao {

	List<Notification> selectNotificationList(String receiverId);

	void createNotification(Notification noiNotification) throws Exception;

	void deleteNotification(int notificationId) throws Exception;

	int selectUnreadNotiCount(String receiverId);
	
	void updateNotificationIsReadColumn(Notification notification) throws Exception;

	Notification selectNotification(int notificationId);
}

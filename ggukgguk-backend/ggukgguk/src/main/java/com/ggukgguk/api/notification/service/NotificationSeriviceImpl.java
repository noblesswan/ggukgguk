package com.ggukgguk.api.notification.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ggukgguk.api.notification.dao.NotificationDao;
import com.ggukgguk.api.notification.vo.Notification;

@Service
public class NotificationSeriviceImpl implements NotificationService {

	@Autowired
	private NotificationDao dao;
	
	private Logger log = LogManager.getLogger("base");
	
	@Override // 현재 로그인한 회원의 알림 리스트 조회
	public List<Notification> ListNotification(String receiverId) {

		try {
			List<Notification> result = dao.selectNotificationList(receiverId);
			return result;
		}catch (Exception e) {
			return null;
		}
	}

	// 알림 생성
	@Override
	public boolean createNotification(Notification notification,String receiverId, String notificationTypeId, int referenceId) {
		// 알림 수신자는 로그인한 회원
		notification.setReceiverId(receiverId);
		
		// 친구 요청 알림
		if(notificationTypeId.equals("FRIEND_REQUEST")) {
			notification.setNotificationTypeId(notificationTypeId);
			notification.setReferenceId(referenceId);  // 친구 요청 테이블의 아이디 값을 넣기.
			try {
				dao.createNotification(notification);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		// 월말 결산 완료
		else if(notificationTypeId.equals("MONTHLY_ANALYSIS")) {
			notification.setNotificationTypeId(notificationTypeId);
			notification.setReferenceId(referenceId);  // 다이어리 테이블의 다이어리 아이디 값 넣기.
			
			try {
				dao.createNotification(notification);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		// 교환일기 요청
		else if(notificationTypeId.equals("EXCHANGE_DIARY")) {
			notification.setNotificationTypeId(notificationTypeId); 
			notification.setReferenceId(referenceId);  // 조각 테이블의  아이디 값  넣기.
			
			try {
				dao.createNotification(notification);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		// 새로운 댓글 알림
		else if(notificationTypeId.equals("NEW_REPLY")) {
			notification.setNotificationTypeId(notificationTypeId); 
			notification.setReferenceId(referenceId);  // 댓글 테이블의  아이디 값  넣기.
			
			try {
				log.debug("입력 전 :" + notification);
				dao.createNotification(notification);
				log.debug("입력 후 :" + notification);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		// 친구 생일 알림
		else if(notificationTypeId.equals("FRIEND_BIRTHDAY")) {
			notification.setNotificationTypeId(notificationTypeId); 
			notification.setReferenceId(referenceId); // ..? (친구 테이블의 아이디 값 넣기?)
			try {
				dao.createNotification(notification);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

	
	// 알림 삭제
	@Override
	public boolean deleteNotify(int notificationId) {
		try {
		dao.deleteNotification(notificationId);	
		}catch (Exception e) {
			return false;
		}
		return true;
	}

	@Override
	public int fetchUnreadNotiCount(String receiverId) {
		try {
			int result = dao.selectUnreadNotiCount(receiverId);
			return result;
		}catch (Exception e) {
			return -1;
		}
	}

	// 알림 읽음 처리
	@Override
	public boolean setReadNotify(Notification notification) {
		try {
			dao.updateNotificationIsReadColumn(notification);	
		}catch (Exception e) {
			return false;
		}
		return true;
	}

	@Override
	public Notification getOriginalNoti(int notificationId) {
		try {
			Notification result = dao.selectNotification(notificationId);
			return result;
		}catch (Exception e) {
			return null;
		}
	}
	
	
}

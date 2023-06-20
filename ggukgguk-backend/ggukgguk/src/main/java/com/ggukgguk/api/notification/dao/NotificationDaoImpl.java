package com.ggukgguk.api.notification.dao;

import java.util.List;

import org.apache.ibatis.javassist.compiler.ast.NewExpr;
import org.apache.ibatis.session.SqlSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ggukgguk.api.notification.vo.Notification;

@Repository
public class NotificationDaoImpl implements NotificationDao {
	
	private Logger log = LogManager.getLogger("base");
	
	@Autowired
	private SqlSession session;
	
	@Override // 회원별 알림 조회
	public List<Notification> selectNotificationList(String receiverId) {
		return session.selectList("com.ggukgguk.api.Notification.selectNotificationList", receiverId);
	}

	
	@Override
	public Notification selectNotification(int notificationId) {
		return session.selectOne("com.ggukgguk.api.Notification.selectNotification", notificationId);
	}


	@Override // 알림 생성
	public void createNotification(Notification noiNotification) throws Exception{
		int result = session.insert("com.ggukgguk.api.Notification.createNotification", noiNotification);
		if(result != 1) {
			throw new Exception();
		}
	}


	@Override // 알림 삭제
	public void deleteNotification(int notificationId) throws Exception {
		int result = session.insert("com.ggukgguk.api.Notification.deleteNotification", notificationId);
	
		if(result != 1) {
			throw new Exception();
		}
	}

	// 읽지 않은 알림 수
	@Override
	public int selectUnreadNotiCount(String receiverId) {
		return session.selectOne("com.ggukgguk.api.Notification.selectUnreadNotiCount", receiverId);
	}


	@Override
	public void updateNotificationIsReadColumn(Notification notification) throws Exception {
		int result = session.insert("com.ggukgguk.api.Notification.updateNotificationIsReadColumn",
				notification);
		
		if(result != 1) {
			throw new Exception();
		}
	}

}

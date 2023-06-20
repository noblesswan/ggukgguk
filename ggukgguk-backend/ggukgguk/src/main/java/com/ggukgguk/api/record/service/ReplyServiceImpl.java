package com.ggukgguk.api.record.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.ggukgguk.api.notification.dao.NotificationDao;
import com.ggukgguk.api.notification.vo.Notification;
import com.ggukgguk.api.record.dao.RecordDao;
import com.ggukgguk.api.record.dao.ReplyDao;
import com.ggukgguk.api.record.vo.Record;
import com.ggukgguk.api.record.vo.Reply;
import com.ggukgguk.api.record.vo.ReplyNickname;

@Service
public class ReplyServiceImpl implements ReplyService {

	@Autowired
	private ReplyDao dao;
	@Autowired
	private RecordDao rdao;
	
	@Autowired
	private NotificationDao notificationDao;
	
	@Autowired
	private PlatformTransactionManager transactionManager;
	
	@Override
	public List<ReplyNickname> addReply(Reply reply) {
		
		TransactionStatus txStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());
		try {
			// 댓글 생성
			dao.insertReply(reply);
			
			// 해당 댓글의  조각 작성자 아이디 값을 가져오기 위해 dao 호출
			Record record = rdao.selectRecord(reply.getRecordId());
			List<ReplyNickname> replyList = dao.selectReplies(reply.getRecordId());
			
//			// 방금 생성한 댓글 테이블의 아이디 값을 가져오기
//			int replyId = (int)reply.getReplyId();	
			
			// 이후 새로운 댓글 알림 생성 
			
			if(reply.getMemberId().equals(record.getMemberId()) || reply.getMemberId().equals(record.getRecordShareTo())) {
				// 알림을 발송하지 않는다.
			} else {
				Notification noti = new Notification(0, "NEW_REPLY", new Date(), record.getRecordId(), record.getMemberId(),0, reply.getMemberId() + "님이 댓글을 남겼습니다.");
				//알림 순번, 알림 타입 = "새로운 댓글". 알림 날짜 , 참조 아이디 = "방금 등록한 댓글의  조각 테이블 아이디", 수신자  = "해당 조각 작성자아이디 ", 수신 여부 = 0, 전달 메시지                
				notificationDao.createNotification(noti);
				
				if(record.getRecordShareTo()!=null) {
					Notification notiShare = new Notification(0, "NEW_REPLY", new Date(), record.getRecordId(), record.getRecordShareTo(),0, reply.getMemberId() + "님이 댓글을 남겼습니다.");
					//알림 순번, 알림 타입 = "새로운 댓글". 알림 날짜 , 참조 아이디 = "방금 등록한 댓글의  조각 테이블 아이디", 수신자  = "해당 조각 작성자아이디 ", 수신 여부 = 0, 전달 메시지                
					notificationDao.createNotification(notiShare);
				}
			}
			
			
			
			
		} catch (Exception e) {
			transactionManager.rollback(txStatus);
			e.printStackTrace();
			return null;
		}	
		transactionManager.commit(txStatus);
		return dao.selectReplies(reply.getRecordId());
	}
	
	@Override
	public List<ReplyNickname> editReply(Reply reply) { 
		
		try {
			dao.updateReply(reply);
			return dao.selectReplies(reply.getRecordId());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public List<ReplyNickname> removeReply(Reply reply) {
		
		try {
			dao.removeReply(reply.getReplyId());
			return dao.selectReplies(reply.getRecordId());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
}

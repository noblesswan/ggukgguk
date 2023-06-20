package com.ggukgguk.api.record.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.multipart.MultipartFile;

import com.ggukgguk.api.member.vo.Member;
import com.ggukgguk.api.notification.dao.NotificationDao;
import com.ggukgguk.api.notification.vo.Notification;
import com.ggukgguk.api.record.dao.RecordDao;
import com.ggukgguk.api.record.vo.MediaFile;
import com.ggukgguk.api.record.vo.Record;
import com.ggukgguk.api.record.vo.RecordSearch;

@Service
public class RecordServiceImpl implements RecordService{

	private Logger log = LogManager.getLogger("base");
	
	@Autowired
	private PlatformTransactionManager transactionManager;
	
	@Autowired
	private RecordDao dao;
	
	@Autowired
	private NotificationDao notiDao;
	
	@Autowired
	private MediaFileService mediaFileService;
	
	@Override
	public List<Record> getRecordList(RecordSearch recordSearch) {
		
		return dao.selectRecordList(recordSearch);
	}

	@Override
	public boolean updateRecord(Record record) {
		
		
        TransactionStatus txStatus =
                transactionManager.getTransaction(new DefaultTransactionDefinition());
		
		try {
			dao.updateRecord(record);
		} catch (Exception e) {
			transactionManager.rollback(txStatus);
			e.printStackTrace();
			return false;
		}
		
		transactionManager.commit(txStatus);
		return true;
	}
	
	public boolean removeRecord(int recordId) {
		
		Record record = dao.selectRecord(recordId);
		
		log.debug(record);
		
        TransactionStatus txStatus =
                transactionManager.getTransaction(new DefaultTransactionDefinition());
		
		try {
			dao.deleteRecord(recordId);
			
		} catch (Exception e) {
			transactionManager.rollback(txStatus);
			e.printStackTrace();
			return false;
		}
		
		// mediaFileId 참조하는 테이블 두개 생김 그것도 삭제해줄것
		
		if(record.getMediaFileId()!=null) {
			try {
				dao.deleteMediaFile(record.getMediaFileId());
			} catch (Exception e) {
				transactionManager.rollback(txStatus);
				e.printStackTrace();
				return false;
			}
		}
	
		transactionManager.commit(txStatus);
		return true;
	}

	@Override
	public boolean saveMediaAndRecord(MultipartFile media, Record record) {
		// 미디어 파일 저장
		MediaFile metadata = null;

		if (media != null) {
			String contentType = media.getContentType();
			System.out.println(contentType);

			String format = contentType.split("/")[0];
			String saveName = (UUID.randomUUID()).toString();
			metadata = new MediaFile(saveName, format, false, false);
			record.setMediaFileId(saveName);

			boolean fileSaveResult = mediaFileService.saveFile(media, format, saveName);
			if (!fileSaveResult) return false;
		}
		
		// DB 업무 (트랜잭션 시작)
        TransactionStatus txStatus =
                transactionManager.getTransaction(new DefaultTransactionDefinition());

    	try {
			if (media != null) dao.insertMediaFile(metadata); // 미디어 파일 메타데이터 추가
			dao.insertRecord(record); // 조각 추가
			
	    	// 교환일기인 경우 알림 저장
	    	String shareTo = record.getRecordShareTo();
	    	if (shareTo != null) {
				int recordId = record.getRecordId();	
				Notification noti = 
						new Notification(0, "EXCHANGE_DIARY", null,
								recordId, shareTo, 0,
								record.getMemberId() + "님이 교환일기를 게시했습니다. 수락하시겠습니까?");
				notiDao.createNotification(noti);
	    	}
		} catch (Exception e) {
			transactionManager.rollback(txStatus);
			e.printStackTrace();
			return false;
		}
    	transactionManager.commit(txStatus);
    	
		return true;
	}
	
	@Override
	public List<Record> getFreindRecordList(RecordSearch recordSearch) {
		
		return dao.selectFriendRecordList(recordSearch);
	}
	
	@Override
	public List<Record> getUnaccepted(String memberId) {
		
		return dao.selectUnaccepted(memberId);
	}
	
	@Override
	public boolean updateUnaccepted(int recordId) {
        TransactionStatus txStatus =
                transactionManager.getTransaction(new DefaultTransactionDefinition());
		
		try {
			dao.updateUnaccepted(recordId);
		} catch (Exception e) {
			transactionManager.rollback(txStatus);
			e.printStackTrace();
			return false;
		}
		
		transactionManager.commit(txStatus);
		return true;
	}

	@Override
	public MediaFile getMediaMetadata(String mediaFileId) {
		return dao.selectMedia(mediaFileId);
	}
	
	@Override
	public Record getRecord(int recordId) {
		
		return dao.selectRecord(recordId);
	}
	
	@Override
	public List<Member> findFriendListByRecord(String memberId) {
		
		return dao.selectFriendListByRecord(memberId);
	}
}

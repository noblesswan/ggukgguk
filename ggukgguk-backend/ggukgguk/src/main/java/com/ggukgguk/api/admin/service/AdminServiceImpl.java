package com.ggukgguk.api.admin.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.ggukgguk.api.admin.dao.AdminDao;
import com.ggukgguk.api.admin.vo.BatchJobExecution;
import com.ggukgguk.api.admin.vo.BatchPageOption;
import com.ggukgguk.api.admin.vo.Content;
import com.ggukgguk.api.admin.vo.ContentDetail;
import com.ggukgguk.api.admin.vo.Main;
import com.ggukgguk.api.admin.vo.MediaClaimPageOption;
import com.ggukgguk.api.admin.vo.MediaFile;
import com.ggukgguk.api.admin.vo.MediaFileRecheckRequest;
import com.ggukgguk.api.admin.vo.Member;
import com.ggukgguk.api.admin.vo.Notice;
import com.ggukgguk.api.common.service.EmailService;
import com.ggukgguk.api.common.vo.PageOption;
import com.ggukgguk.api.common.vo.TotalAndListPayload;
import com.ggukgguk.api.notification.vo.Notification;

@Service
public class AdminServiceImpl implements AdminService {
	
	private Logger log = LogManager.getLogger("base");
	
	@Autowired
	private AdminDao dao;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private PlatformTransactionManager transactionManager;

	@Override
	public List<Notice> noticeSelectPage(PageOption option) {
		return dao.noticeSelectPaging(option);
	}

	public boolean noticeWrite(Notice notice) {
		try {
			dao.noticeInsert(notice);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean noticeRead(int noticeId) {
		try {
			dao.noticeSelect(noticeId);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean noticeUpdate(Notice notice) {
		try {
			dao.updateNotice(notice);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean noticeDelete(int noticeId) {
		try {
			dao.deleteNotice(noticeId);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public TotalAndListPayload contentSelectPage(PageOption option) {
		TotalAndListPayload payload = new TotalAndListPayload(
			dao.contentSelectCount(option),
			dao.contentSelectPaging(option)
		);
		
		return payload;
	}

	@Override
	public TotalAndListPayload memberSelectPage(PageOption option) {
		TotalAndListPayload payload = new TotalAndListPayload(
				dao.memberSelectCount(option),
				dao.memberSelectPaging(option)
			);
			return payload;
	}

	// 회원 삭제
	@Override
	public boolean memberDelete(String memberId) {
		try {
			dao.memberDelete(memberId);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	@Override
	public Main mainAdmin() {
		Main main = new Main();
		try {
			int totalMember = dao.totalMember();
			main.setTotalMember(totalMember);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			int todayMember = dao.todayMember();
			main.setTodayMember(todayMember);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			int totalContent = dao.totalContent();
			main.setTotalContent(totalContent);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			int todayContent = dao.todayContent();
			main.setTodayContent(todayContent);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return main;
	}

	@Override
	public List<ContentDetail> recordRead(int recordId) {
		return dao.recordSelectList(recordId);
	}

//	// 페이징 처리를 위한 전체 게시글 리스트 조회
//	@Override
//	public TotalAndListPayload getMemberList(PageOption option) {
//		TotalAndListPayload payload = new TotalAndListPayload();
//		payload.setList(dao.selectMemberList(option)); // 전체 회원 리스트 조회
//		payload.setTotal(dao.selectMemberListTotal(option)); // 페이징 처리를 위한 전체회원 수 구하기
//		return payload;
//	}
	public Map<String, List<BatchJobExecution>> fetchBatchStatus() {
		Map<String, List<BatchJobExecution>> resultMap =
				new HashMap<String, List<BatchJobExecution>>();
		
		resultMap.put("extractKeywordJob", dao.selectRecentBatchJobExecution("extractKeywordJob"));
		resultMap.put("checkModContentJob", dao.selectRecentBatchJobExecution("checkModContentJob"));

		return resultMap;
	}

	@Override
	public TotalAndListPayload fetchBatchStatusByJobName(BatchPageOption option) {
		TotalAndListPayload payload = new TotalAndListPayload();
		
		payload.setTotal(dao.selectBatchJobExecutionCount(option));
		payload.setList(dao.selectBatchJobExecution(option));
		
		return payload;
	}

	@Override
	public Map<String, Object> getDailyReportAll(String startDate, String endDate) {
		Map<String, String> option = new HashMap<String, String>();
		option.put("startDate", startDate);
		option.put("endDate", endDate);
		
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("newMember", dao.selectMemberDailyReport(option));
		result.put("newRecord", dao.selectRecordDailyReport(option));
		result.put("newReply", dao.selectReplyDailyReport(option));
		
		return result;
	}

	@Override
	public List<Map<String, Integer>> getDailyReport(String startDate, String endDate, String reportSubject) {
		Map<String, String> option = new HashMap<String, String>();
		option.put("startDate", startDate);
		option.put("endDate", endDate);
		
		List<Map<String, Integer>> result = null;
		
		switch (reportSubject) {
		case "member":
			result = dao.selectMemberDailyReport(option);
			break;

		case "record":
			result = dao.selectRecordDailyReport(option);
			break;
			
		case "reply":
			result = dao.selectReplyDailyReport(option);
			break;
		default:
			result = null;
			break;
		}
		
		return result;
	}

	@Override
	public TotalAndListPayload getMediaClaim(MediaClaimPageOption option) {
		TotalAndListPayload payload = new TotalAndListPayload(
			dao.selectMediaClaimCount(option),
			dao.selectMediaClaim(option)
		);
		
		return payload;
	}

	@Override
	public MediaFile getMediaDetail(MediaFile option) {
		return dao.selectMediaExtended(option);
	}

	@Override
	public boolean addMediaClaim(MediaFileRecheckRequest payload) {
		try {
			dao.insertMediaClaim(payload);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean editMediaClaim(MediaFileRecheckRequest payload) {
		
		// DB 업무 (트랜잭션 시작)
        TransactionStatus txStatus =
                transactionManager.getTransaction(new DefaultTransactionDefinition());

    	try {
			if ("PASSED".equals(payload.getMediaFileRecheckRequestStatus())) {
				// 통과됨 상태로 변경된 경우 블록을 해제해준다
				
				MediaFile mediaFile = new MediaFile();
				mediaFile.setMediaFileId(payload.getMediaFileId());
				mediaFile.setMediaFileBlocked(false);
				
				dao.updateShouldMediaBlocked(mediaFile);
			}
			dao.updateMediaClaim(payload);
		} catch (Exception e) {
			transactionManager.rollback(txStatus);
			e.printStackTrace();
			log.debug("[ADMIN SERVICE] DB 업무 실패");
			return false;
		}
    	transactionManager.commit(txStatus);
    	
    	try {
			emailService.sendEmail(payload.getMemberEmail(), 
					"차단 미디어 이의제기 처리 현황이 변경되었습니다",
					"<div>고객님 안녕하세요, 꾹꾹입니다. <br> 고객님께서 작성하신 미디어 차단 이의제기의 처리 현황이 변경되었습니다.<br>"
					+ "                    <li>이의제기 ID: " + payload.getMediaFileRecheckRequestId() + "</li>\r\n" + 
					"                    <li>미디어 파일 ID: " + payload.getMediaFileId() + "</li>\r\n" + 
					"                    <li>미디어 타입: " + payload.getMediaTypeId() + "</li>\r\n" + 
					"                    <li>요청 본문: " + payload.getMediaFileRecheckRequestClaim() + "</li>\r\n" + 
					"                    <li>처리 현황: " + payload.getMediaFileRecheckRequestStatus() + "</li>\r\n" + 
					"                    <li>처리 결과: " + payload.getMediaFileRecheckRequestReply() + "</li>"
					+ "<a href='https://app.ggukgguk.online/login'><button style='background-color: #50A73A; border: none; "
					+ "color: white; padding: 10px 20px; text-align: center; display: inline-block; "
					+ "margin: 4px 2px; cursor: pointer;'>꾹꾹 접속하기 </button> </a></div>");
		} catch (Exception e) {
			e.printStackTrace();
			log.debug("[ADMIN SERVICE] 메일 전송 실패");
			return false;
		}
		
		return true;
	}

	@Override
	public boolean patchMediaDetail(MediaFile payload) {
		try {
			dao.updateShouldMediaBlocked(payload);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}

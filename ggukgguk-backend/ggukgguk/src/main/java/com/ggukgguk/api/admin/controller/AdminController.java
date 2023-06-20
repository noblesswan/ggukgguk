package com.ggukgguk.api.admin.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ggukgguk.api.admin.service.AdminService;
import com.ggukgguk.api.admin.vo.BatchJobExecution;
import com.ggukgguk.api.admin.vo.BatchPageOption;
import com.ggukgguk.api.admin.vo.ContentDetail;
import com.ggukgguk.api.admin.vo.Main;
import com.ggukgguk.api.admin.vo.MediaClaimPageOption;
import com.ggukgguk.api.admin.vo.MediaFile;
import com.ggukgguk.api.admin.vo.MediaFileRecheckRequest;
import com.ggukgguk.api.admin.vo.Notice;
import com.ggukgguk.api.common.vo.BasicResp;
import com.ggukgguk.api.common.vo.PageOption;
import com.ggukgguk.api.common.vo.TotalAndListPayload;
import com.ggukgguk.api.diary.service.DiaryService;
import com.ggukgguk.api.record.service.RecordService;
import com.ggukgguk.api.record.service.ReplyService;
import com.ggukgguk.api.record.vo.Reply;
import com.ggukgguk.api.record.vo.ReplyNickname;

@RestController
@RequestMapping("/admin")
public class AdminController {
	private Logger log = LogManager.getLogger("base");

	@Autowired
	AdminService adminService;

	@Autowired
	DiaryService diaryService;
	
	@Autowired
	ReplyService replyService;
	
	@Autowired
	RecordService recordService;
	
	// admin main페이지
	@GetMapping("")
	public ResponseEntity<?> noticeaddHandler() {
		BasicResp<Object> respBody;
		Main result = adminService.mainAdmin();
		log.debug(result);
		if (result != null) {
			log.debug("admim page 조회 성공");
			respBody = new BasicResp<Object>("success", "admim page 조회 성공", result);
			return ResponseEntity.ok(respBody);
		} else {
			log.debug("admim page 조회 실패");
			respBody = new BasicResp<Object>("error", "admim page 조회 실패", null);
			return ResponseEntity.badRequest().body(respBody);
		}
	}

	// 전체 게시글 리스트 조회
	@GetMapping("/notice/list")
	public ResponseEntity<?> noticeSelectPageHandler(@RequestParam("page") int page, @RequestParam("size") int size) {
		PageOption option = new PageOption();
		option.setPage(page);
		option.setSize(size);
		List<Notice> result = adminService.noticeSelectPage(option);

		BasicResp<Object> respBody = null;
		int respCode = 0;

		if (result != null) {
			Map<String, Object> payload = new HashMap<String, Object>();
			payload.put("list", result);

			respBody = new BasicResp<Object>("true", "게시글 조회 성공", payload);
			respCode = HttpServletResponse.SC_OK;
		} else {
			respBody = new BasicResp<Object>("false", "게시글 조회 실패", null);
			respCode = HttpServletResponse.SC_BAD_REQUEST;
		}

		return new ResponseEntity<Object>(respBody, null, respCode);
	}

	// 공지사항 게시글 작성
	@PostMapping("/notice/write")
	public ResponseEntity<?> noticeWirteHandler(@RequestBody Notice notice) {
		BasicResp<Object> respBody;
		boolean result = adminService.noticeWrite(notice);
		
		if (result) {
			log.debug("게시글 작성 성공");
			respBody = new BasicResp<Object>("success", "게시글 작성에 실패하였습니다", null);
			return ResponseEntity.ok(respBody);
		} else {
			log.debug("게시글 작성 실패");
			respBody = new BasicResp<Object>("error", "게시글 작성에 실패하였습니다", null);
			return ResponseEntity.badRequest().body(respBody);
		}
	}

	// 공지사항 게시글 읽기
	@GetMapping("/notice/read/{noticeId}")
	public ResponseEntity<?> noticeReadHandler(@PathVariable int noticeId) {
		BasicResp<Object> respBody;
		boolean result = adminService.noticeRead(noticeId);

		if (result) {
			log.debug("게시글 읽기 성공");
			respBody = new BasicResp<Object>("success", "게시글 읽기 성공", null);
			return ResponseEntity.ok(respBody);
		} else {
			log.debug("게시글 읽기 실패");
			respBody = new BasicResp<Object>("error", "게시글 읽기에 실패하였습니다", null);
			return ResponseEntity.badRequest().body(respBody);
		}
	}

	// 공지사항 게시글 수정
	@PutMapping("/notice/update/{noticeId}")
	public ResponseEntity<?> noticeUpdateHandler(@PathVariable int noticeId, @RequestBody Notice notice) {
		BasicResp<Object> respBody;
		notice.setNoticeId(noticeId);
		boolean result = adminService.noticeUpdate(notice);

		if (result) {
			log.debug("게시글 수정 성공");
			respBody = new BasicResp<Object>("success", "게시글 수정 성공", null);
			return ResponseEntity.ok(respBody);
		} else {
			log.debug("게시글 수정 실패");
			respBody = new BasicResp<Object>("error", "게시글 수정 실패", null);
			return ResponseEntity.badRequest().body(respBody);
		}
	}

	// 공지사항 게시글 삭제
	@DeleteMapping("notice/delete/{noticeId}")
	public ResponseEntity<?> noticeDeleteHandler(@PathVariable int noticeId) {
		BasicResp<Object> respBody;
		boolean result = adminService.noticeDelete(noticeId);

		if (result) {
			log.debug("게시글 삭제 성공");
			respBody = new BasicResp<Object>("success", "게시글 삭제 성공", null);
			return ResponseEntity.ok(respBody);
		} else {
			log.debug("게시글 삭제 실패");
			respBody = new BasicResp<Object>("error", "게시글 삭제 실패", null);
			return ResponseEntity.badRequest().body(respBody);
		}
	}
	
	// 컨텐츠관리 리스트 조회
	@GetMapping("/content")
	public ResponseEntity<?> contentListHandler(@RequestParam("page") int page, @RequestParam("size") int size) {
		PageOption option = new PageOption();
		option.setPage(page);
		option.setSize(size);
		TotalAndListPayload result = adminService.contentSelectPage(option);
		
		BasicResp<Object> respBody = null;
		int respCode = 0;

		if (result != null) {
			respBody = new BasicResp<Object>("true", "컨텐츠 조회 성공", result);
			respCode = HttpServletResponse.SC_OK;
		} else {
			respBody = new BasicResp<Object>("false", "컨텐츠 조회 실패", null);
			respCode = HttpServletResponse.SC_BAD_REQUEST;
		}

		return new ResponseEntity<Object>(respBody, null, respCode);
	}	
	
	// 컨텐츠관리 리스트 조회
	@GetMapping("/member")
	public ResponseEntity<?> memberListHandler(@RequestParam("page") int page, @RequestParam("size") int size) {
		PageOption option = new PageOption();
		option.setPage(page);
		option.setSize(size);
		TotalAndListPayload result = adminService.memberSelectPage(option);
		
		BasicResp<Object> respBody = null;
		int respCode = 0;

		if (result != null) {
			respBody = new BasicResp<Object>("true", "멤버 조회 성공", result);
			respCode = HttpServletResponse.SC_OK;
		} else {
			respBody = new BasicResp<Object>("false", "멤버 조회 실패", null);
			respCode = HttpServletResponse.SC_BAD_REQUEST;
		}

		return new ResponseEntity<Object>(respBody, null, respCode);
	}	
//	// 회원관리 리스트 조회
//	@GetMapping("/member")
//	public ResponseEntity<?> membertListHandler(@RequestParam("page") int page, @RequestParam("size") int size) {
//		PageOption option = new PageOption();
//		option.setPage(page);
//		option.setSize(size);
//		List<Member> result = adminService.memberSelectPage(option);
//		
//		BasicResp<Object> respBody = null;
//		int respCode = 0;
//
//		if (result != null) {
//			Map<String, Object> payload = new HashMap<String, Object>();
//			payload.put("list", result);
//
//			respBody = new BasicResp<Object>("true", "멤버관리 조회 성공", payload);
//			respCode = HttpServletResponse.SC_OK;
//		} else {
//			respBody = new BasicResp<Object>("false", "멤버관리 조회 실패", null);
//			respCode = HttpServletResponse.SC_BAD_REQUEST;
//		}
//
//		return new ResponseEntity<Object>(respBody, null, respCode);
//	}	
	
	
	
	
	// 회원 삭제 
	@PutMapping("/member/delete/{memberId}")
	public ResponseEntity<?> memberDeleteHandler(@PathVariable String memberId) {
		BasicResp<Object> respBody;
		boolean result = adminService.memberDelete(memberId);

		if (result) {
			log.debug("회원 삭제 성공");
			respBody = new BasicResp<Object>("success", "회원 삭제 성공", null);
			return ResponseEntity.ok(respBody);
		} else {
			log.debug("회원 삭제 실패");
			respBody = new BasicResp<Object>("error", "회원 삭제 실패", null);
			return ResponseEntity.badRequest().body(respBody);
		}
	}
	
	// 조각 상세 조회
	@GetMapping("/content/{recordId}")
	public ResponseEntity<?> recordReadHandler(@PathVariable int recordId) {
		BasicResp<Object> respBody;
		List<ContentDetail> result = adminService.recordRead(recordId);

		if (result != null) {
			log.debug("게시글 읽기 성공");
			respBody = new BasicResp<Object>("success", "게시글 읽기 성공", result);
			return ResponseEntity.ok(respBody);
		} else {
			log.debug("게시글 읽기 실패");
			respBody = new BasicResp<Object>("error", "게시글 읽기에 실패하였습니다", null);
			return ResponseEntity.badRequest().body(respBody);
		}
	}
	
	// 조각 삭제
	@DeleteMapping("record/{recordId}")
	public ResponseEntity<?> removeRecord(@PathVariable int recordId){
		
		BasicResp<Object> respBody;
		boolean result = recordService.removeRecord(recordId);
		
		if (result) {
			log.debug("게시글 삭제 성공");
			respBody = new BasicResp<Object>("success", null, null);
			return ResponseEntity.ok(respBody);
		} else {
			log.debug("게시글 삭제 실패");
			respBody = new BasicResp<Object>("error", "게시글 삭제에 실패하였습니다.", null);		
			return ResponseEntity.badRequest().body(respBody);
		}
	}
	
	// 댓글 삭제
	@DeleteMapping("/reply/{replyId}")
	public ResponseEntity<?> removeReply(@PathVariable int replyId, @RequestBody Reply reply){
	
		reply.setReplyId(replyId);
		BasicResp<Object> respBody;
		List<ReplyNickname> replyList = replyService.removeReply(reply);
		log.debug("삭제컨트롤러");
		
		if (replyList != null) {
			log.debug("댓글 삭제 성공");
			respBody = new BasicResp<Object>("success", null, replyList);
			return ResponseEntity.ok(respBody);
		} else {
			log.debug("댓글 삭제 실패");
			respBody = new BasicResp<Object>("error", "댓글 삭제에 실패하였습니다.", null);		
			return ResponseEntity.badRequest().body(respBody);
		}
	}	

	/**
	 * 각 배치의 최근 실행된 인스턴스의 실행 현황을 조회한다
	 */
	@GetMapping("/batch")
	public ResponseEntity<?> getBatchStatusHandler() {
		BasicResp<Object> respBody;
		Map<String, List<BatchJobExecution>> result = adminService.fetchBatchStatus();

		if (result != null) {
			log.debug("배치 현황 조회 성공");
			respBody = new BasicResp<Object>("success", null, result);
			return ResponseEntity.ok(respBody);
		} else {
			log.debug("배치 현황 조회 실패");
			respBody = new BasicResp<Object>("error", "배치 현황 조회 실패", null);
			return ResponseEntity.badRequest().body(respBody);
		}
	}
	
	/**
	 * 잡별 실행 이력을 조회한다.
	 * @return
	 */
	@GetMapping("/batch/{jobName}")
	public ResponseEntity<?> getBatchStatusByJobNameHandler(@ModelAttribute BatchPageOption option) {
		BasicResp<Object> respBody;
		log.debug(option);
		TotalAndListPayload payload = adminService.fetchBatchStatusByJobName(option);

		if (payload != null) {
			log.debug("배치 세부 현황 조회 성공");
			respBody = new BasicResp<Object>("success", null, payload);
			return ResponseEntity.ok(respBody);
		} else {
			log.debug("배치 세부 현황 조회 실패");
			respBody = new BasicResp<Object>("error", "배치 세부 현황 조회 실패", null);
			return ResponseEntity.badRequest().body(respBody);
		}
	}
	
	@GetMapping("/report/daily")
	public ResponseEntity<?> getDailyAllHandler(@RequestParam(value="startDate", required = false) String startDate,
			@RequestParam(value="endDate", required = false) String endDate) {
		BasicResp<Object> respBody;

		Map<String, Object> result = adminService.getDailyReportAll(startDate, endDate);

		if (result != null) {
			log.debug("일자별 데이터 조회 성공");
			respBody = new BasicResp<Object>("success", null, result);
			return ResponseEntity.ok(respBody);
		} else {
			log.debug("일자별 데이터 조회 실패");
			respBody = new BasicResp<Object>("error", "일자별 데이터 조회 실패", null);
			return ResponseEntity.badRequest().body(respBody);
		}
	}
	
	@GetMapping("/report/daily/{reportSubject}")
	public ResponseEntity<?> getDailyHandler(@RequestParam(value="startDate", required = false) String startDate,
			@RequestParam(value="endDate", required = false) String endDate,
			@PathVariable String reportSubject) {
		BasicResp<Object> respBody;
		
		if (!reportSubject.equals("member")
				&& !reportSubject.equals("record")
				&& !reportSubject.equals("reply")) {
			log.debug("일자별 데이터 조회 실패");
			respBody = new BasicResp<Object>("error", "일자별 데이터 조회 실패 (NO_SUCH_ITEM)", null);
			return ResponseEntity.badRequest().body(respBody);
		}

		List<Map<String, Integer>> result = adminService.getDailyReport(startDate, endDate, reportSubject);

		if (result != null) {
			log.debug("일자별 데이터 조회 성공 " + reportSubject);
			respBody = new BasicResp<Object>("success", null, result);
			return ResponseEntity.ok(respBody);
		} else {
			log.debug("일자별 데이터 조회 실패 " + reportSubject);
			respBody = new BasicResp<Object>("error", "일자별 데이터 조회 실패", null);
			return ResponseEntity.badRequest().body(respBody);
		}
	}
	
	/**
	 * 미디어 파일 재심사 요청 조회
	 * @param option
	 * @return
	 */
	@GetMapping("/content/claim")
	public ResponseEntity<?> getMediaClaimHandler(@ModelAttribute MediaClaimPageOption option) {
		BasicResp<Object> respBody;

		TotalAndListPayload result = adminService.getMediaClaim(option);

		if (result != null) {
			log.debug("재심사 요청 데이터 조회 성공");
			respBody = new BasicResp<Object>("success", null, result);
			return ResponseEntity.ok(respBody);
		} else {
			log.debug("재심사 요청 데이터 조회 실패");
			respBody = new BasicResp<Object>("error", "재심사 요청 조회 실패", null);
			return ResponseEntity.badRequest().body(respBody);
		}
	}
	
	/**
	 * 재심사 요청 작성
	 * 일반 사용자 접근 가능하도록 풀어줘야 함
	 * @param payload
	 * @return
	 */
	@PostMapping("/content/claim")
	public ResponseEntity<?> addMediaClaimHandler(@RequestBody MediaFileRecheckRequest payload) {
		BasicResp<Object> respBody;
		
		if (payload.getMediaFileId() == null || "".equals(payload.getMediaFileId())) {
			log.debug("재심사 요청 작성 실패 - 미디어 파일 아이디가 없습니다");
			respBody = new BasicResp<Object>("error", "재심사 요청 작성에 실패하였습니다 (EMPTY_MEDIA_ID)", null);
			return ResponseEntity.badRequest().body(respBody);
		}
		if (payload.getMediaFileRecheckRequestClaim() == null || "".equals(payload.getMediaFileRecheckRequestClaim())) {
			log.debug("재심사 요청 작성 실패 - 요청 본문이 없습니다");
			respBody = new BasicResp<Object>("error", "재심사 요청 작성에 실패하였습니다 (EMPTY_REQUEST_CONTENT)", null);
			return ResponseEntity.badRequest().body(respBody);
		}
		payload.setMediaFileRecheckRequestStatus("BEFORE");
		
		boolean result = adminService.addMediaClaim(payload);
		
		if (result) {
			log.debug("재심사 요청 작성 성공");
			respBody = new BasicResp<Object>("success", "재심사 요청 작성에 성공하였습니다", null);
			return ResponseEntity.ok(respBody);
		} else {
			log.debug("재심사 요청 작성 실패");
			respBody = new BasicResp<Object>("error", "재심사 요청 작성에 실패하였습니다", null);
			return ResponseEntity.badRequest().body(respBody);
		}
	}
	
	@PutMapping("/content/claim/{claimId}")
	public ResponseEntity<?> editMediaClaimHandler(@PathVariable String claimId,
			@RequestBody MediaFileRecheckRequest payload) {
		BasicResp<Object> respBody;
		
		int claimIdParsed = -1;
		try {
			claimIdParsed = Integer.parseInt(claimId);
		} catch (NumberFormatException e) {
			log.debug("재심사 요청 수정 실패 - 재심사 아이디가 잘못되었습니다");
			respBody = new BasicResp<Object>("error", "재심사 요청 수정에 실패하였습니다 (REUQEST_ID_NOT_NUMBER)", null);
			return ResponseEntity.badRequest().body(respBody);
		}
		payload.setMediaFileRecheckRequestId(claimIdParsed);
		
		boolean result = adminService.editMediaClaim(payload);
		
		if (result) {
			log.debug("재심사 요청 수정 성공");
			respBody = new BasicResp<Object>("success", "재심사 요청 작성에 성공하였습니다", null);
			return ResponseEntity.ok(respBody);
		} else {
			log.debug("재심사 요청 수정 실패");
			respBody = new BasicResp<Object>("error", "재심사 요청 작성에 실패하였습니다", null);
			return ResponseEntity.badRequest().body(respBody);
		}
	}
	
	/**
	 * 미디어 파일 세부정보(차단 이력 포함)
	 * @param mediaFileId
	 * @return
	 */
	@GetMapping("/content/media/{mediaFileId}")
	public ResponseEntity<?> getMediaDetailHandler(@PathVariable("mediaFileId") String mediaFileId) {
		BasicResp<Object> respBody;

		MediaFile option = new MediaFile();
		option.setMediaFileId(mediaFileId);
		
		MediaFile result = adminService.getMediaDetail(option);

		if (result != null) {
			log.debug("미디어 파일 세부정보 데이터 조회 성공");
			respBody = new BasicResp<Object>("success", null, result);
			return ResponseEntity.ok(respBody);
		} else {
			log.debug("미디어 파일 세부정보 데이터 조회 실패");
			respBody = new BasicResp<Object>("error", "미디어 파일 세부정보 요청 조회 실패", null);
			return ResponseEntity.badRequest().body(respBody);
		}
	}

	/**
	 * 미디어 파일 차단 여부 수정 
	 * @param mediaFileId
	 * @return
	 */
	@PatchMapping("/content/media/{mediaFileId}")
	public ResponseEntity<?> patchMediaDetailHandler(@PathVariable("mediaFileId") String mediaFileId,
			@RequestBody MediaFile mediaFile) {
		BasicResp<Object> respBody;
		
		mediaFile.setMediaFileId(mediaFileId);
		
		boolean result = adminService.patchMediaDetail(mediaFile);

		if (result) {
			log.debug("미디어 파일 차단 여부 수정 성공");
			respBody = new BasicResp<Object>("success", null, result);
			return ResponseEntity.ok(respBody);
		} else {
			log.debug("미디어 파일 차단 여부 수정 실패");
			respBody = new BasicResp<Object>("error", "미디어 파일 차단 여부 수정 실패", null);
			return ResponseEntity.badRequest().body(respBody);
		}
	}
}

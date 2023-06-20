package com.ggukgguk.api.admin.service;

import java.util.List;
import java.util.Map;

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
import com.ggukgguk.api.common.vo.PageOption;
import com.ggukgguk.api.common.vo.TotalAndListPayload;

public interface AdminService {

	// 공지사항 리스트 출력
	public List<Notice> noticeSelectPage(PageOption option);

	// 게시글 작성
    public boolean noticeWrite(Notice notice);

    // 게시글 조회
	public boolean noticeRead(int noticeId);

	// 게시글 수정
	public boolean noticeUpdate(Notice notice);

	// 게시글 삭제
	public boolean noticeDelete(int noticeId);

	// 컨텐츠 리스트 출력
	public TotalAndListPayload contentSelectPage(PageOption option);

	public TotalAndListPayload memberSelectPage(PageOption option);

	// 회원삭제
	public boolean memberDelete(String memberId);

	Main mainAdmin();

	public List<ContentDetail> recordRead(int recordId);

//	public TotalAndListPayload getMemberList(PageOption option);
	// 최근 배치 현황 조회
	public Map<String, List<BatchJobExecution>> fetchBatchStatus();

	public TotalAndListPayload fetchBatchStatusByJobName(BatchPageOption option);

	public Map<String, Object> getDailyReportAll(String startDate, String endDate);

	public List<Map<String, Integer>> getDailyReport(String startDate, String endDate, String reportSubject);

	public TotalAndListPayload getMediaClaim(MediaClaimPageOption option);

	public MediaFile getMediaDetail(MediaFile option);

	public boolean addMediaClaim(MediaFileRecheckRequest payload);

	public boolean editMediaClaim(MediaFileRecheckRequest payload);

	public boolean patchMediaDetail(MediaFile option);
	
}

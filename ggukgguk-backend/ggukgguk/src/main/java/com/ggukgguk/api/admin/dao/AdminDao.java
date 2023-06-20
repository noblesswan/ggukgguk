package com.ggukgguk.api.admin.dao;

import java.util.List;
import java.util.Map;

import com.ggukgguk.api.admin.vo.BatchJobExecution;
import com.ggukgguk.api.admin.vo.BatchPageOption;
import com.ggukgguk.api.admin.vo.Content;
import com.ggukgguk.api.admin.vo.ContentDetail;
import com.ggukgguk.api.admin.vo.MediaClaimPageOption;
import com.ggukgguk.api.admin.vo.MediaFile;
import com.ggukgguk.api.admin.vo.MediaFileRecheckRequest;
import com.ggukgguk.api.admin.vo.Member;
import com.ggukgguk.api.admin.vo.Notice;
import com.ggukgguk.api.common.vo.PageOption;

public interface AdminDao {
    public void noticeInsert(Notice notice) throws Exception;

	public void noticeSelect(int noticeId)throws Exception;

	public void updateNotice(Notice notice) throws Exception;

	public void deleteNotice(int noticeId) throws Exception;

	public List<Notice> noticeSelectPaging(PageOption option);

	public List<Content> contentSelectPaging(PageOption option);
	
	public List<Member> memberSelectPaging(PageOption option);

	public void memberDelete(String memberId) throws Exception;

	//
	
	public int totalMember() throws Exception;
	public int todayMember() throws Exception;
	public int totalContent() throws Exception;
	public int todayContent() throws Exception;

	public List<ContentDetail> recordSelectList(int recordId);

	public int contentSelectCount(PageOption option);
	
	public int memberSelectCount(PageOption option);

//	public List<?> selectMemberList(PageOption option);

//	public int selectMemberListTotal(PageOption option);

	public List<BatchJobExecution> selectRecentBatchJobExecution(String jobName);

	public List<BatchJobExecution> selectBatchJobExecution(BatchPageOption option);

	public int selectBatchJobExecutionCount(BatchPageOption option);

	public List<Map<String, Integer>> selectMemberDailyReport(Map<String, String> option);

	public List<Map<String, Integer>> selectRecordDailyReport(Map<String, String> option);

	public List<Map<String, Integer>> selectReplyDailyReport(Map<String, String> option);

	public List<MediaFileRecheckRequest> selectMediaClaim(MediaClaimPageOption option);

	public int selectMediaClaimCount(MediaClaimPageOption option);

	public MediaFile selectMediaExtended(MediaFile option);

	public void insertMediaClaim(MediaFileRecheckRequest payload) throws Exception;

	public void updateMediaClaim(MediaFileRecheckRequest payload) throws Exception;

	public void updateShouldMediaBlocked(MediaFile payload) throws Exception;

}


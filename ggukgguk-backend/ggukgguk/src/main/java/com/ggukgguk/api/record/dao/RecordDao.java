package com.ggukgguk.api.record.dao;

import java.util.List;

import com.ggukgguk.api.member.vo.Member;
import com.ggukgguk.api.record.vo.MediaFile;
import com.ggukgguk.api.record.vo.Record;
import com.ggukgguk.api.record.vo.RecordSearch;

public interface RecordDao {

	List<Record> selectRecordList(RecordSearch recordSearch);

	void deleteRecord(int recordId) throws Exception;

	void insertMediaFile(MediaFile metadata) throws Exception;

	void insertRecord(Record record) throws Exception;

	List<Record> selectFriendRecordList(RecordSearch recordSearch);

	void updateRecord(Record record) throws Exception;

	Record selectRecord(int recordId);

	void deleteReplyList(int recordId) throws Exception;

	void deleteMediaFile(String mediaFileId) throws Exception;

	void deleteKeyword(int recordId) throws Exception;

	int selectKeyword(int recordId);

	List<Record> selectUnaccepted(String memberId);

	void updateUnaccepted(int recordId) throws Exception;

	MediaFile selectMedia(String mediaFileId);

	List<Member> selectFriendListByRecord(String memberId);

}

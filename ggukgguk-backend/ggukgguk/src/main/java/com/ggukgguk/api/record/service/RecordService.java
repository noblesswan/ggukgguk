package com.ggukgguk.api.record.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.ggukgguk.api.member.vo.Member;
import com.ggukgguk.api.record.vo.MediaFile;
import com.ggukgguk.api.record.vo.Record;
import com.ggukgguk.api.record.vo.RecordSearch;

public interface RecordService {

	public List<Record> getRecordList(RecordSearch recordSearch);

	public boolean removeRecord(int recordId);
	
	public boolean saveMediaAndRecord(MultipartFile media, Record record);

	public List<Record> getFreindRecordList(RecordSearch recordSearch);

	public boolean updateRecord(Record record);

	public List<Record> getUnaccepted(String memberId);

	public boolean updateUnaccepted(int recordId);

	public MediaFile getMediaMetadata(String fileId);

	public Record getRecord(int recordId);

	public List<Member> findFriendListByRecord(String memberId);

}

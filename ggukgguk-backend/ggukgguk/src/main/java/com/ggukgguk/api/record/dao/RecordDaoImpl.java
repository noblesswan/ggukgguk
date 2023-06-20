package com.ggukgguk.api.record.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ggukgguk.api.member.vo.Member;
import com.ggukgguk.api.record.vo.MediaFile;
import com.ggukgguk.api.record.vo.Record;
import com.ggukgguk.api.record.vo.RecordSearch;

@Repository
public class RecordDaoImpl implements RecordDao{

	@Autowired
	SqlSession session;
	
	@Override
	public List<Record> selectRecordList(RecordSearch recordSearch) {
		
		return session.selectList("com.ggukgguk.api.Record.selectList", recordSearch);
	}
	
	@Override
	public Record selectRecord(int recordId) {
		
		return session.selectOne("com.ggukgguk.api.Record.selectOne", recordId);
	}
	
	@Override
	public void updateRecord(Record record) throws Exception {
		
		int affectedRow = session.update("com.ggukgguk.api.Record.update", record);
		
		if (affectedRow != 1) {
			throw new Exception();
		}
		
	}
	
	@Override
	public void deleteReplyList(int recordId) throws Exception {

		int affectedRow = session.delete("com.ggukgguk.api.Record.deleteReplyList", recordId);
		
		if (affectedRow != 1) {
			throw new Exception();
		}
	}
	
	@Override
	public int selectKeyword(int recordId) {
		
		return session.selectOne("com.ggukgguk.api.Record.selectKeywordCount", recordId);
	}
	
	@Override
	public void deleteKeyword(int recordId) throws Exception {
		int affectedRow = session.delete("com.ggukgguk.api.Record.deleteKeyword", recordId);
		
		if (affectedRow != 1) {
			throw new Exception();
		}
	}
	
	@Override
	public void deleteMediaFile(String mediaFileId) throws Exception {

		int affectedRow = session.delete("com.ggukgguk.api.Record.deleteMediaFile", mediaFileId);
		
		if (affectedRow != 1) {
			throw new Exception();
		}
		
	}
	
	@Override
	public void deleteRecord(int recordId) throws Exception {

		int affectedRow = session.delete("com.ggukgguk.api.Record.delete", recordId);
		
		if (affectedRow != 1) {
			throw new Exception();
		}
		
	}

	@Override
	public void insertMediaFile(MediaFile metadata) throws Exception {
		
		int affectedRow = session.delete("com.ggukgguk.api.Record.insertMedia", metadata);
		
		if (affectedRow != 1) {
			throw new Exception();
		}
	}

	@Override
	public void insertRecord(Record record) throws Exception {
		
		int affectedRow = session.delete("com.ggukgguk.api.Record.insertRecord", record);
		
		if (affectedRow != 1) {
			throw new Exception();
		}
	}
	
	@Override
	public List<Record> selectFriendRecordList(RecordSearch recordSearch) {
		
		return session.selectList("com.ggukgguk.api.Record.selectFriendRecordList", recordSearch);
	}
	
	@Override
	public List<Record> selectUnaccepted(String memberId) {
		
		return session.selectList("com.ggukgguk.api.Record.selectUnaccepted", memberId);
	}
	
	@Override
	public void updateUnaccepted(int recordId) throws Exception {
		
		int affectedRow = session.update("com.ggukgguk.api.Record.updateUnaccepted", recordId);
		
		if (affectedRow != 1) {
			throw new Exception();
		}
	}

	@Override
	public MediaFile selectMedia(String mediaFileId) {

		return session.selectOne("com.ggukgguk.api.Record.selectMedia", mediaFileId);
	}
	
	@Override
	public List<Member> selectFriendListByRecord(String memberId) {
		
		return session.selectList("com.ggukgguk.api.Record.selectFriendListByRecord", memberId);
	}
}

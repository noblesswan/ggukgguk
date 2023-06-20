package com.ggukgguk.api.record.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ggukgguk.api.record.vo.Reply;
import com.ggukgguk.api.record.vo.ReplyNickname;

@Repository
public class ReplyDaoImpl implements ReplyDao {

	@Autowired
	SqlSession session;
	
	@Override
	public void insertReply(Reply reply) throws Exception {
		
		int affectedRow = session.insert("com.ggukgguk.api.Reply.insert", reply);
		
		if(affectedRow != 1) {
			throw new Exception();
		}
		
	}
	
	@Override
	public List<ReplyNickname> selectReplies(int recordId) {
		
		return session.selectList("com.ggukgguk.api.Reply.selectList", recordId);
	}
	
	@Override
	public void updateReply(Reply reply) throws Exception {
		
		int affectedRow = session.update("com.ggukgguk.api.Reply.update", reply);
		
		if(affectedRow != 1) {
			throw new Exception();
		}
	}
	
	@Override
	public void removeReply(int replyId) throws Exception {
		
		int affectedRow = session.delete("com.ggukgguk.api.Reply.delete", replyId);
		
		if(affectedRow != 1) {
			throw new Exception();
		}
		
	}

}

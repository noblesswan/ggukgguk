package com.ggukgguk.api.record.dao;

import java.util.List;

import com.ggukgguk.api.record.vo.Reply;
import com.ggukgguk.api.record.vo.ReplyNickname;


public interface ReplyDao {

	void insertReply(Reply reply) throws Exception;

	List<ReplyNickname> selectReplies(int recordId);

	void updateReply(Reply reply) throws Exception;

	void removeReply(int replyId) throws Exception;

}

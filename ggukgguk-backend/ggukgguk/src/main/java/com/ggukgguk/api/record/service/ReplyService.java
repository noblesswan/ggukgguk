package com.ggukgguk.api.record.service;

import java.util.List;

import com.ggukgguk.api.record.vo.Reply;
import com.ggukgguk.api.record.vo.ReplyNickname;

public interface ReplyService {

	List<ReplyNickname> addReply(Reply reply);

	List<ReplyNickname> editReply(Reply reply);

	List<ReplyNickname> removeReply(Reply reply);

}

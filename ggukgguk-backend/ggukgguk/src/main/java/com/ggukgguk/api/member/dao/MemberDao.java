package com.ggukgguk.api.member.dao;

import java.util.List;

import com.ggukgguk.api.common.vo.PageOption;
import com.ggukgguk.api.member.vo.Friend;
import com.ggukgguk.api.member.vo.FriendRequest;
import com.ggukgguk.api.member.vo.Member;
import com.ggukgguk.api.member.vo.Verify;
import com.ggukgguk.api.record.vo.RecordSearch;

public interface MemberDao {

	public Member selectMemberById(String memberId);

	public void insertMember(Member member) throws Exception;

	public Member selectMemberByEmail(String memberEmail);

	public Member selectMemberByEmailandId(Member member);

	public Member updateMemberInfo(Member member) throws Exception;

	public List<?> selectMemberList(PageOption option);

	public int selectMemberListTotal(PageOption option);

	public void requestFriend(FriendRequest request) throws Exception;

	public void newRelationship(Friend friend) throws Exception;

	public FriendRequest selectFriendRequestList(FriendRequest friendRequest);
	
	public List<Member> selectFindPartOfId(String memberId);

	public List<Member> selectFriendList(String mymemberId);

	public void breakRelationship(Friend friend) throws Exception;

	public void deleteFriendRequeset(int friendRequestId) throws Exception;

	public int selectFriendship(RecordSearch recordSearch);

	public void insertEmailAuthenticationCode(Verify verify)throws Exception;

	public Verify authenticationMatch(Verify verify);

	public List<FriendRequest> selectRequestFriendList(FriendRequest friendRequestId);

	public void pwModify(Member member) throws Exception;
}

package com.ggukgguk.api.member.service;

import java.util.List;

import com.ggukgguk.api.common.vo.PageOption;
import com.ggukgguk.api.common.vo.TotalAndListPayload;
import com.ggukgguk.api.member.vo.Friend;
import com.ggukgguk.api.member.vo.FriendRequest;
import com.ggukgguk.api.member.vo.Member;
import com.ggukgguk.api.member.vo.Verify;
import com.ggukgguk.api.record.vo.RecordSearch;

public interface MemberService {

	public Member findMemberById(String memberId);

	public boolean enrollMember(Member member);

	public Member getMemberByEmail(String memberEmail);

	public boolean getMemberByEmailandId(Member member);

	public Member modifyMember(Member member);

	public TotalAndListPayload getMemberList(PageOption option);

	public boolean requestFriend(FriendRequest request);

	public boolean acceptFriend(Friend friend, FriendRequest friendRequest, String fromMemberId, String toMemberId);

	public List<Member> findmyFriend(String memberId);

	public List<Member> findFriendList(String mymemberId);

	public boolean breakFriend(String myMemberId, String toMemberid, Friend friend);

	public boolean checkDuplicateId(String memberId);

	public boolean getFriendship(RecordSearch recordSearch);
	
	public boolean getFriendship(String member1, String member2);

	public boolean getCheckAuthenticationCode(String certificationNumber, String storedAuthCode);

	public boolean getCheckTableAuthenticationCode(Verify verify, String sendTo, String certificationNumber);

	public boolean postMemberAuthenticationCode(Verify verify, String authenticationCode, String sendTo);

	public boolean postPasswordAuthenticationCode(Verify verify, String authenticationCode, String sendTo);

	public List<FriendRequest> findRequestFriendList(FriendRequest friendRequest, String myMemberId,int friendRequestId);

	public boolean redefinePw(Member member);

}

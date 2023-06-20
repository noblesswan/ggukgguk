package com.ggukgguk.api.member.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ggukgguk.api.common.vo.PageOption;
import com.ggukgguk.api.member.vo.Friend;
import com.ggukgguk.api.member.vo.FriendRequest;
import com.ggukgguk.api.member.vo.Member;
import com.ggukgguk.api.member.vo.Verify;
import com.ggukgguk.api.record.vo.RecordSearch;

@Repository
public class MemberDaoImpl implements MemberDao {
	
	private Logger log = LogManager.getLogger("base");
	
	@Autowired
	private SqlSession session;
	
	// 친구 찾기, 회원정보 찾기
	@Override
	public Member selectMemberById(String memberId) {
		return session.selectOne("com.ggukgguk.api.Member.selectById", memberId);
	}

	@Override // 회원 가입방식
	public void insertMember(Member member) throws Exception{
		log.debug("테스트");
		int count = session.insert("com.ggukgguk.api.Member.registerMember",member);
		
		if (count != 1) {
			throw new Exception();
		}
	}
	@Override // 아이디 찾기  = 이메일 값으로 부터 찾기
	public Member selectMemberByEmail(String memberEmail) {
		return session.selectOne("com.ggukgguk.api.Member.selectByEmail",memberEmail);
	}

	@Override // 비밀번호 찾기 
	public Member selectMemberByEmailandId(Member member) {
		return session.selectOne("com.ggukgguk.api.Member.selectByPassword", member);
	}

	
//	@Override // 회원정보 수정
//	public void updateMemberInfo(Member member) throws Exception {
//		log.debug(member);
//		int updateMember = session.update("com.ggukgguk.api.Member.updateMemberInfo", member);
//	//		session.selectOne("com.ggukgguk.api.Member.selectByEmail",member.getMemberEmail());
//		if(updateMember != 1) {
//			throw new Exception();
//		}
//	}
//
//	
	@Override // 회원정보 수정
	public Member updateMemberInfo(Member member) throws Exception {
		log.debug(member);
		int updateMember = session.update("com.ggukgguk.api.Member.updateMemberInfo", member);

		if(updateMember != 1) {
			throw new Exception();
		}
		return session.selectOne("com.ggukgguk.api.Member.selectByEmail",member.getMemberEmail());
	}
	
	@Override // 전체 회원 리스트 
	public List<?> selectMemberList(PageOption option) {
		return session.selectList("com.ggukgguk.api.Member.totalMemberList",option);
	}

	@Override // 페이징 처리를 위한 전체 회원 수 구하기
	public int selectMemberListTotal(PageOption option) {
		return session.selectOne("com.ggukgguk.api.Member.selectMemberTotal", option);
	}

	@Override // 친구요청
	public void requestFriend(FriendRequest request) throws Exception {
		int result = session.insert("com.ggukgguk.api.Member.requestFriend", request);
		if(result != 1) {
			throw new Exception();
		}
	}	
	
	@Override // 친구 요청 테이블 조회
	public FriendRequest selectFriendRequestList(FriendRequest friendRequest) {
		return session.selectOne("com.ggukgguk.api.Member.selectRequestFriendList", friendRequest);
	}

	// 친구 수락
	@Override
	public void newRelationship(Friend friend) throws Exception {
		int result = session.insert("com.ggukgguk.api.Member.createFriend", friend);
		if(result != 1) {
			throw new Exception();
		}
		
	}
	// 친구 요청 테이블 행 삭제
	@Override
	public void deleteFriendRequeset(int friendRequestId) throws Exception {
		int result = session.delete("com.ggukgguk.api.Member.deleteRequestFriend", friendRequestId);
		if(result != 1) {
			throw new Exception();
		}
		
	}
	// 친구 아이디 찾기 (일부분 값으로)
	@Override
	public List<Member> selectFindPartOfId(String memberId) {
		return session.selectList("com.ggukgguk.api.Member.selectFindPartOfId", memberId);
		
	}

	// 현재 친구 목록조회 
	@Override
	public List<Member> selectFriendList(String myMemberId){
		return session.selectList("com.ggukgguk.api.Member.selectFriendList", myMemberId);
		
	}

	// 친구 관계 차단
	@Override
	public void breakRelationship(Friend friend) throws Exception {
		int result = session.delete("com.ggukgguk.api.Member.deleteRelationship",friend);
		if(result != 1) {
			throw new Exception();
		}
	}
	
	@Override
	public int selectFriendship(RecordSearch recordSearch) {
		
		return session.selectOne("com.ggukgguk.api.Member.selectFriendship", recordSearch);
	}

	// 이메일 인증코드 넣기
	@Override
	public void insertEmailAuthenticationCode(Verify verify) throws Exception {
		int result = session.insert("com.ggukgguk.api.Verify.insertAuthenticationCode", verify);
		if(result != 1) {
			throw new Exception();
		}
	}
	
	// DB에서 인증코드 확인하기
	@Override
	public Verify authenticationMatch(Verify verify) {
		return session.selectOne("com.ggukgguk.api.Verify.selectAuthenticationCode", verify);
	}

	
	// 친구 요청테이블 조회 [나한테만 온것들]
	@Override
	public List<FriendRequest> selectRequestFriendList(FriendRequest friendRequest) {
		return session.selectList("com.ggukgguk.api.Member.selectMyRequestFriendList", friendRequest);
	
	}

	// 비밀번호 수정
	@Override
	public void pwModify(Member member) throws Exception {
		log.debug(member);
		int updateMember = session.update("com.ggukgguk.api.Member.modifyPw",member);
		if(updateMember != 1) {
			throw new Exception();
		}

	}
}

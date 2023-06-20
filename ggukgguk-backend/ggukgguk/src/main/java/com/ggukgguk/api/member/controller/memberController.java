package com.ggukgguk.api.member.controller;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ggukgguk.api.common.vo.PageOption;
import com.ggukgguk.api.common.vo.BasicResp;
import com.ggukgguk.api.common.vo.TotalAndListPayload;
import com.ggukgguk.api.member.service.MemberService;
import com.ggukgguk.api.member.vo.Member;

@RestController
@RequestMapping(value="/member")
public class memberController {
	private Logger log = LogManager.getLogger("base");
	
	@Autowired
	private MemberService Memberservice;
	
	// 회원정보 조회
	@GetMapping(value = "/{memberId}") 
	public ResponseEntity<?> getMemberInfo(@PathVariable String memberId) {
		BasicResp<Object> respBody;
		Member result = Memberservice.findMemberById(memberId);
		if (!result.equals(null)) {
			respBody = new BasicResp<Object>("success", "회원 정보 조회를 성공하였습니다.", result);
			return ResponseEntity.ok(respBody);
		} else {
			respBody = new BasicResp<Object>("error", "회원 정보 조회를 실패하였습니다.", null);
			return ResponseEntity.badRequest().body(respBody);
		}

	}
	// 회원 정보 수정
	@PutMapping(value= "/{memberEmail}")
	public ResponseEntity<?> updateMemberInfo(@RequestBody Member member,@PathVariable String memberEmail){
		log.debug(memberEmail);
		BasicResp<Object> respBody;
		member.setMemberEmail(memberEmail);
		// boolean에서 Member vo로 반환값을 받는 이유, 만약  회원 아이디 값을 변경시 프런트에서  store에 setMemberInfo 값에도 바뀐 아이디 정보롤 저장학기 위해서
//		boolean result = Memberservice.modifyMember(member); 
		Member result = Memberservice.modifyMember(member); 
		if (!result.equals(null)) {
			respBody = new BasicResp<Object>("success", "회원 정보를 수정하였습니다.", result);
			return ResponseEntity.ok(respBody);
		} else {
			respBody = new BasicResp<Object>("error", "회원 정보를 수정에 실패하였습니다.", null);
			return ResponseEntity.badRequest().body(respBody);
		}
	}
	// 전체 회원 조회 (관리자)
	@GetMapping("")
	public ResponseEntity<?> getMemberListHandler(@ModelAttribute PageOption option){
		log.debug(option);
		
		BasicResp<Object> respBody;
		TotalAndListPayload payload = Memberservice.getMemberList(option);
		
		if (payload != null) {
			log.debug("회원 리스트 조회 성공");
			
			respBody = new BasicResp<Object>("success", null, payload);
			return ResponseEntity.ok(respBody);
		} else {
			log.debug("회원 리스트 조회 실패");
			respBody = new BasicResp<Object>("error", "전체 회원 조회에 실패하였습니다.", null);		
			return ResponseEntity.badRequest().body(respBody);
		}	
	}	
}

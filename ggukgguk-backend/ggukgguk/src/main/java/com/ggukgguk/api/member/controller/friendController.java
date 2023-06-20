package com.ggukgguk.api.member.controller;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import com.ggukgguk.api.auth.vo.AuthTokenPayload;
import com.ggukgguk.api.common.service.EmailService;
import com.ggukgguk.api.common.vo.BasicResp;
import com.ggukgguk.api.member.service.MemberService;
import com.ggukgguk.api.member.vo.Friend;
import com.ggukgguk.api.member.vo.FriendRequest;
import com.ggukgguk.api.member.vo.Member;
import com.ggukgguk.api.member.vo.Verify;

@RestController
@RequestMapping("/friend")
public class friendController {
	private Logger log = LogManager.getLogger("base");

	AuthTokenPayload Payload;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private MemberService memberservice;

	// 친구 요청
	@PostMapping(value = "/send/{toMemberId}")
	public ResponseEntity<?> friendHandler(@PathVariable String toMemberId, Authentication authentication,
			@ModelAttribute FriendRequest request) {
		BasicResp<Object> respBody;
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		String myMemberId = userDetails.getUsername();
		request.setFromMemberId(myMemberId);
		request.setToMemberId(toMemberId);
		boolean result = memberservice.requestFriend(request);
		if (result) {
			respBody = new BasicResp<Object>("success", "친구 요청을 성공하였습니다.", result);
			return ResponseEntity.ok(respBody);
		} else {
			respBody = new BasicResp<Object>("error", "친구 요청에 실패하였습니다.", null);
			return ResponseEntity.badRequest().body(respBody);
		}
	}

	// 친구 수락
	@PostMapping(value = "/accept/{toMemberId}")
	public ResponseEntity<?> friendAccept(@PathVariable String toMemberId, @ModelAttribute Friend friend,
			@ModelAttribute FriendRequest friendRequest, Authentication authentication) {
		BasicResp<Object> respBody;
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		String myMemberId = userDetails.getUsername(); // 로그인한 사용자 ID 값을 받아오기

		boolean result = memberservice.acceptFriend(friend, friendRequest, toMemberId, myMemberId);
		if (result) {
			respBody = new BasicResp<Object>("success", "친구 수락을 성공하였습니다..", result);
			return ResponseEntity.ok(respBody);
		} else {
			respBody = new BasicResp<Object>("error", "친구 수락에 실패하였습니다.", null);
			return ResponseEntity.badRequest().body(respBody);
		}
	}

	// 친구 찾기
	@GetMapping(value = "")
	public ResponseEntity<?> findFriend(@RequestParam String memberId, Authentication authentication) {
		BasicResp<Object> respBody;	

		List<Member> result = memberservice.findmyFriend(memberId);
		if (!result.equals(null)) {
			respBody = new BasicResp<Object>("success", "친구 찾기를 성공하였습니다.", result);
			return ResponseEntity.ok(respBody);
		} else {
			respBody = new BasicResp<Object>("error", "친구 찾기를 실패하였습니다.", null);
			return ResponseEntity.badRequest().body(respBody);
		}
	}

	// 친구 목록 조회
	@GetMapping(value = "/list")
	public ResponseEntity<?> friendList(Authentication authentication) {
		BasicResp<Object> respBody;
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		String myMemberId = userDetails.getUsername();

		List<Member> result = memberservice.findFriendList(myMemberId);
		if (result != null) {
			respBody = new BasicResp<Object>("success", "나의 친구 목록을 조회 성공하였습니다.", result);
			return ResponseEntity.ok(respBody);
		} else {
			respBody = new BasicResp<Object>("error", "나의 친구 목록을 조회 실패하였습니다.", null);
			return ResponseEntity.badRequest().body(respBody);
		}
	}
	
	// 친구 차단
	@DeleteMapping(value = "/{toMemberId}")
	public ResponseEntity<?> breakFriends(Authentication authentication, @PathVariable String toMemberId,
			@ModelAttribute Friend friend) {
		BasicResp<Object> respBody;
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		String myMemberId = userDetails.getUsername();

		boolean result = memberservice.breakFriend(myMemberId, toMemberId, friend);

		if (result) {
			respBody = new BasicResp<Object>("success", "친구를 차단하였습니다.", result);
			return ResponseEntity.ok(respBody);
		} else {
			respBody = new BasicResp<Object>("error", "친구를 차단하지 못하였습니다.", null);
			return ResponseEntity.badRequest().body(respBody);
		}
	}

	// 친구 요청 테이블  조회
	@GetMapping(value = "/requestFriendlist")
	public ResponseEntity<?> requestFriendList(Authentication authentication,
			@ModelAttribute FriendRequest friendRequest, @RequestParam int friendRequestId) {
		BasicResp<Object> respBody;
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		String myMemberId = userDetails.getUsername();

		List<FriendRequest> result = memberservice.findRequestFriendList(friendRequest, myMemberId ,friendRequestId);
		if (result != null) {
			respBody = new BasicResp<Object>("success", " 요청 친구 목록을 조회 성공하였습니다.", result);
			return ResponseEntity.ok(respBody);
		} else {
			respBody = new BasicResp<Object>("error", "요청 친구 목록을 조회 실패하였습니다.", null);
			return ResponseEntity.badRequest().body(respBody);
		}
	}
	
	
	// 친구 추가시 상대방의 친구 요청 안내 메일 전송
	@GetMapping(value = "/mailCertification", produces = "application/json; charset=UTF-8")
	public ResponseEntity<?> cetificationPostPwMail(@RequestParam String sendTo) throws Exception {
		
		
		BasicResp<Object> resp = null;
		if (sendTo == null || sendTo.equals("")) {
			resp = new BasicResp<Object>("success", "수신자 메일이 잘못되었습니다.", null);
			return ResponseEntity.badRequest().body(resp);
		}
		
		boolean result = emailService.sendEmail(sendTo,
				"꾹꾹  알림  메일입니다.",
				"<div>당신과 친구 맺기를 희망하는 회원이있습니다 <br>"+
				"꾹꾹 서비스에 접속하시여 친구 요청 알림 메시지를 확인 바랍니다.<br>"+
				"<a href='https://app.ggukgguk.online/login'><button style='background-color: #50A73A; border: none; "
				+ "color: white; padding: 10px 20px; text-align: center; display: inline-block; "
				+ "margin: 4px 2px; cursor: pointer;'>꾹꾹 접속하기 </button> </a></div>");
		if (result) {
			resp = new BasicResp<Object>("success", null,result);
			return ResponseEntity.ok(resp);
		} else {
			resp = new BasicResp<Object>("success", "메일 전송에 실패했습니다.", null);
			return ResponseEntity.badRequest().body(resp);
		}
	}
	
	
}

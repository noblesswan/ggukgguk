package com.ggukgguk.api.auth.controller;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
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
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.ggukgguk.api.auth.service.AuthService;
import com.ggukgguk.api.auth.service.OAuthService;
import com.ggukgguk.api.auth.vo.AuthTokenPayload;
import com.ggukgguk.api.common.service.EmailService;
import com.ggukgguk.api.common.vo.BasicResp;
import com.ggukgguk.api.common.vo.GenerateCertCharacter;
import com.ggukgguk.api.common.vo.PageOption;
import com.ggukgguk.api.common.vo.TotalAndListPayload;
import com.ggukgguk.api.member.service.MemberService;
import com.ggukgguk.api.member.vo.Member;
import com.ggukgguk.api.member.vo.Verify;
import com.nimbusds.jose.shaded.json.JSONArray;

@RestController
@RequestMapping(value = "/auth")
public class AuthController {
	private Logger log = LogManager.getLogger("base");

	@Autowired
	private AuthService service;
	@Autowired
	private MemberService memberSerivce;
	@Autowired
	private EmailService emailService;

	@Autowired
	private OAuthService oauth;

	// 일반 로그인 방식
	@PostMapping(value = "/login")
	public ResponseEntity<?> loginHandler(@RequestBody Member reqLoginInfo) {
		BasicResp<Object> respBody = null;

		final AuthTokenPayload payload = service.login(reqLoginInfo);

		if (payload == null) {
			log.debug("로그인 실패");
			respBody = new BasicResp<Object>("error", "로그인에 실패하였습니다.", null);
			return ResponseEntity.badRequest().body(respBody);
		} else {
			log.debug("로그인 성공");
			respBody = new BasicResp<Object>("success", null, payload);
			return ResponseEntity.ok(respBody);
		}
	}

	// 인증 핸들러? (JWT 접근시 토큰을 갱신 목적)
	@PostMapping(value = "/refresh")
	public ResponseEntity<?> verifyHandler(@RequestBody HashMap<String, String> reqPayload) {

		AuthTokenPayload respPayload = service.regenToken(reqPayload.get("refreshToken"));

		BasicResp<?> respBody = null;
		int respCode = 0;
		if (respPayload == null) {
			respBody = new BasicResp<Object>("error", "토큰을 재발급하는데 실패했습니다.", null);
			return ResponseEntity.badRequest().body(respBody);
		} else {
			respBody = new BasicResp<Object>("success", null, respPayload);
			return ResponseEntity.ok(respBody);
		}
	}

	// 소셜로그인 전반적인 흐름도
	// [전반적인 흐름 예상] 만약 프런트에서 Rest_API_Key와 Redirect url 주소를 반환하여 접근 코드를 건네줌 => 해당
	// 코드를 접근 토큰으로 반환하여 백엔드 전송.

	// [사용형태]
	// kauth.kakao.com/oauth/authorize?client_id={Rest_API키}&redirect_uri={Redirect
	// URI 주소}&response_type=code
	// kauth.kakao.com/oauth/authorize?client_id=88ae00c6ba4b777f197c6d3b5c972acd&redirect_uri=http://localhost:8080/api/auth/social/kakao&response_type=code

	// 위의 url을 실행하면
	// http://localhost:8080/api/auth/kakao?code= 카카오 서비스에서 준 인가 코드
	// http://localhost:8080/api/auth/kakao?code=NFA9gSuZgIwCUkHC1306Mlndn6ilaRhvSMRboppO3jbm1gjIsn2def9KzxfsZaNtoft66Qo9c-sAAAGHk3H7yg

	// 이제 백엔드에서는
	// 서버에서 받은 access_token을 이용하여 카카오 서버에서 사용자 정보를 받음
	// 2. 받은 사용자 정보를 이용하여 회원가입 또는 로그인을 진행함

	// 참고 주소 : https://suyeoniii.tistory.com/79

	// 카카오 로그인 방식
	@GetMapping(value = "/social/kakao")
	public ResponseEntity<?> kakaoCallback(@RequestParam String AccessToken) throws Exception {
		BasicResp<Object> respBody;
		Member result = oauth.kakaoLogin(AccessToken);
		result.setMemberPw("kakao");
		AuthTokenPayload tknPayload = service.login(result); // 기존의 꾹꾹 로그인 서비스 방식인 JWT방식과 연동? 접목하기 위해서 설정

		if (result != null) {
			log.debug("카카오  성공");
			respBody = new BasicResp<Object>("success", "카카오 로그인 성공하였습니다.", tknPayload);
			return ResponseEntity.ok(respBody);
		} else {
			log.debug("카카오 실패");
			respBody = new BasicResp<Object>("error", "실패하였습니다.", null);
			return ResponseEntity.badRequest().body(respBody);
		}

	}
	// 구글의 소셜로그인위 위에서 서령한 방식가 동일한 형태.
	// [사용형태]
	// https://accounts.google.com/o/oauth2/auth?client_id={CLIENT_ID}&redirect_uri={REDIRECT_URI}&response_type=code&scope=https://www.googleapis.com/auth/userinfo.email
	// https://www.googleapis.com/auth/userinfo.profile
	// 1. 나의 ID와 리다이렉팅할 주소를 삽입 후 위 주소를 실행하면 리다이렉팅으로 인가 코드를 받음
	// https://accounts.google.com/o/oauth2/auth?client_id=720876072203-9qs394kg6d2ekko35ln9h0pil109lvft.apps.googleusercontent.com&redirect_uri=http://localhost:8080/api/auth/social/google&response_type=code&scope=https://www.googleapis.com/auth/userinfo.email
	// https://www.googleapis.com/auth/userinfo.profile
	// 2. 인가 코드 받음
	// http://localhost:8080/api/auth/google?code={구글 인가 코드 }&scope=동의하는 범위 및 목록...
	// http://localhost:8080/api/auth/google?code=4%2F0AVHEtk5QQtcheSNO6hMPC1Sh1gH5ht_shwjWwbCcZMFtH4qj1k10O5BchwxLMloQ-F8zGQ&scope=email+profile+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.profile+openid+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email&authuser=0&prompt=none
	// 3. 코드를 통해 접근 통큰 발급하여 토큰을 통해 사용자 정보 반환.
	// 참고, https://darrenlog.tistory.com/40

//	// 구글 로그인 방식
//	@GetMapping("/social/google")
//	public ResponseEntity<?> googleCallback(@RequestParam String code)  {
//		BasicResp<Object> respBody;
////		String token = oauth.getGoogleAccessToken(code);
////		JsonNode result = oauth.getGoogleUserInfo(token);
//		log.debug("테스트 :" + code);
//		Member result = oauth.googleLogin(code);
//		if (result != null) {
//			log.debug("구글 정보 반환 성공");
//			respBody = new BasicResp<Object>("success", "구글 사용자로 로그인합니다.", result);
//			return ResponseEntity.ok(respBody);
//		} else {
//			log.debug("구글 정보 반환 실패");
//			respBody = new BasicResp<Object>("error", "로그인 되지 않습니다.", null);
//			return ResponseEntity.badRequest().body(respBody);
//		}
//	}

	@GetMapping("/social/google")
	public ResponseEntity<?> googleCallback(@RequestParam String token) {
		BasicResp<Object> respBody;
		log.debug(token);
		Member result = oauth.googleLogin(token);
		result.setMemberPw("google");
		AuthTokenPayload tknPayload = service.login(result);
		log.debug(tknPayload);

		if (result != null) {
			respBody = new BasicResp<Object>("success", "구글 사용자로 로그인합니다.", tknPayload);
			return ResponseEntity.ok(respBody);
		} else {
			respBody = new BasicResp<Object>("error", "로그인 되지 않습니다.", null);
			return ResponseEntity.badRequest().body(respBody);
		}
	}

	// 꾹꾹 서비스단 에서 일반적인 회원가입
	@PostMapping("/register")
	public ResponseEntity<?> registerHandler(@RequestBody Member member) {
		BasicResp<Object> respBody;
		boolean result = memberSerivce.enrollMember(member);
		log.debug(member);
		if (result) {
			log.debug("회원 가입 등록");
			respBody = new BasicResp<Object>("success", "등록되었습니다.", result);
			return ResponseEntity.ok(respBody);
		} else {
			log.debug("회원 가입 실패");
			respBody = new BasicResp<Object>("error", "등록되지 않았습니다.", null);
			return ResponseEntity.badRequest().body(respBody);
		}
	}

	// 아이디 중복 검사
	@GetMapping("/exist/{memberId}")
	public ResponseEntity<?> getduplicatecheckId(@PathVariable String memberId) {
		BasicResp<Object> respBody;
		boolean result = memberSerivce.checkDuplicateId(memberId);

		if (result) {
			log.debug("아이디 중복");
			respBody = new BasicResp<Object>("success", "아이디  중복 되었습니다.", result);
			return ResponseEntity.ok(respBody);
		} else {
			log.debug("아이디 중복되지 않았습니다.");
			respBody = new BasicResp<Object>("error", "아이디가 중복되지 않았습니다.", null);
			return ResponseEntity.ok(respBody);
		}
	}

	// 아이디 찾기 (이메일 주소로 DB에 있는 회원 ID찾기)
	@GetMapping("/{memberEmail}")
	public ResponseEntity<?> getMemberIdHandler(@PathVariable String memberEmail) {
		BasicResp<Object> respBody;
		Member result = memberSerivce.getMemberByEmail(memberEmail);

		if (!result.equals(null)) {
			log.debug("아이디 찾기 완료");
			respBody = new BasicResp<Object>("success", "아이디 찾기 완료 하였습니다.", result);
			return ResponseEntity.ok(respBody);
		} else {
			log.debug("아이디 찾기 실패");
			respBody = new BasicResp<Object>("error", "아이디 찾기 실패하였습니다.", null);
			return ResponseEntity.badRequest().body(respBody);
		}
	}

	// 비밀번호 찾기
	@GetMapping("")
	public ResponseEntity<?> getMemberIdHandler(@RequestParam String memberEmail, @RequestParam String memberId,
			@ModelAttribute Member member) {
		BasicResp<Object> respBody;
		member.setMemberEmail(memberEmail);
		member.setMemberId(memberId);
		log.debug(member);
		Boolean result = memberSerivce.getMemberByEmailandId(member);

		if (result) {
			log.debug("아이디 찾기 완료");
			respBody = new BasicResp<Object>("success", "가입된 회원 입니다.", result);
			return ResponseEntity.ok(respBody);
		} else {
			log.debug("아이디 찾기 실패");
			respBody = new BasicResp<Object>("error", "가입된 회원이 아닙니다.", result);
			return ResponseEntity.badRequest().body(respBody);
		}
	}

	// 임의의 문자와 숫자 8자리로 생성하도록 클래스를 따로 만듬.
//	private GenerateCertCharacter generateCertCharacter;
//	private GenerateCertCharacter generateCertCharacter;
	// 메모리의 캐시 형태로 하여 이메일 인증코드와 사용자(받는 사람이메일)랑 같이 저장.
	private ConcurrentHashMap<String, String> authCodeCache = new ConcurrentHashMap<>();

	// 회원 가입 시 인증번호 메일 전송
	@GetMapping(value = "/mailCertification", produces = "application/json; charset=UTF-8")
	public ResponseEntity<?> cetificationPostMail(@RequestParam String sendTo, @ModelAttribute Verify verify)
			throws Exception {
		GenerateCertCharacter generateCertCharacter = new GenerateCertCharacter();
		String authenticationCode = generateCertCharacter.excuteGenerate();
		log.debug(authenticationCode);
		authCodeCache.put(sendTo, authenticationCode); // 회원 이메일과 인증 코드를 key,value쌍으로 저장.

		BasicResp<Object> resp = null;
		if (sendTo == null || sendTo.equals("")) {
			resp = new BasicResp<Object>("success", "수신자 메일이 잘못되었습니다.", null);
			return ResponseEntity.badRequest().body(resp);
		}

		boolean result = emailService.sendEmail(sendTo, "꾹꾹 가입 인증 메일입니다.",
				"<div>아래의 인증 번호를 입력하여 가입하시면 가입이 완료됩니다..<br> 인증 번호는 : " + authenticationCode
						+ " 입니다 확인 후 페이지 입력해 주세요.</div>");

		// 회원 가입 or 비밀전호 찾기 시 메일 주소 확인 및 인증번호 db 테이블에 저장.
		boolean verifyInsert = memberSerivce.postMemberAuthenticationCode(verify, authenticationCode, sendTo);

		if (verifyInsert) {
			resp = new BasicResp<Object>("success", null, verifyInsert);
			return ResponseEntity.ok(resp);
		} else {
			resp = new BasicResp<Object>("success", "메일 전송에 실패했습니다.", null);
			return ResponseEntity.badRequest().body(resp);
		}
	}

	// 비밀번호 찾기 시 인증번호 메일 전송
	@GetMapping(value = "/mailCertificationPw", produces = "application/json; charset=UTF-8")
	public ResponseEntity<?> cetificationPostPwMail(@RequestParam String sendTo, @ModelAttribute Verify verify)
			throws Exception {
		GenerateCertCharacter generateCertCharacter = new GenerateCertCharacter();
		String authenticationCode = generateCertCharacter.excuteGenerate();
		log.debug(authenticationCode);
		
		authCodeCache.put(sendTo, authenticationCode); // 회원 이메일과 인증 코드를 key,value쌍으로 저장.

		BasicResp<Object> resp = null;
		if (sendTo == null || sendTo.equals("")) {
			resp = new BasicResp<Object>("success", "수신자 메일이 잘못되었습니다.", null);
			return ResponseEntity.badRequest().body(resp);
		}

		boolean result = emailService.sendEmail(sendTo, "꾹꾹  비밀번호 찾기 인증 메일입니다.",
				"<div>아래의 인증 번호를 입력하여 비밀번호를 찾으시기 바랍니다..<br> 인증 번호는 : " + authenticationCode
						+ " 입니다 확인 후 페이지 입력해 주세요.</div>");

		// 회원 가입 or 비밀전호 찾기 시 메일 주소 확인 및 인증번호 db 테이블에 저장.
		boolean verifyInsert = memberSerivce.postPasswordAuthenticationCode(verify, authenticationCode, sendTo);

		if (verifyInsert) {
			resp = new BasicResp<Object>("success", null, verifyInsert);
			return ResponseEntity.ok(resp);
		} else {
			resp = new BasicResp<Object>("success", "메일 전송에 실패했습니다.", null);
			return ResponseEntity.badRequest().body(resp);
		}
	}

	// 비밀번호 찾기 및 회원가입시 이메일 인증 코드 확인
	@GetMapping(value = "/mailCertificationNumberCheck", produces = "application/json; charset=UTF-8")
	public ResponseEntity<?> checkCertification(@ModelAttribute Verify verify, @RequestParam String sendTo,
			@RequestParam String certificationNumber) throws Exception {
		BasicResp<Object> resp = null;

		String storedAuthCode = authCodeCache.get(sendTo); // 이메일 인증코드로 보낸 코드를 가져오기
	
		// 1안. 메모리의 캐시 형태로 하여 이메일 인증코드와 사용자(받는 사람이메일)랑 같이 저장.
		// boolean result =
		// memberSerivce.getCheckAuthenticationCode(certificationNumber,storedAuthCode);

		// 2안. DB에 저장하여 조회하는 방식.
		boolean result = memberSerivce.getCheckTableAuthenticationCode(verify, sendTo, certificationNumber);

		if (result) {
			resp = new BasicResp<Object>("success", null, result);
			return ResponseEntity.ok(resp);
		} else {
			resp = new BasicResp<Object>("success", "코드 인증을 실패했습니다.", result);
			return ResponseEntity.badRequest().body(resp);
		}
	}

	// 비밀번호 변경
	@PutMapping(value = "/editPw")
	public ResponseEntity<?> checkCertification(@RequestBody Member member) throws Exception {
		BasicResp<Object> resp = null;


		// 2안. DB에 저장하여 조회하는 방식.
		boolean result = memberSerivce.redefinePw(member);

		if (result) {
			resp = new BasicResp<Object>("success", null, result);
			return ResponseEntity.ok(resp);
		} else {
			resp = new BasicResp<Object>("success", "코드 인증을 실패했습니다.", result);
			return ResponseEntity.badRequest().body(resp);
		}
	}

}

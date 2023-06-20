package com.ggukgguk.api.auth.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.impl.MementoMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.*;
import org.springframework.security.config.annotation.authentication.configurers.userdetails.DaoAuthenticationConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.ggukgguk.api.common.vo.BasicResp;
import com.ggukgguk.api.member.dao.MemberDao;
import com.ggukgguk.api.member.vo.Member;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nimbusds.jose.shaded.json.JSONObject;

@Service
public class OAuthService {
	private Logger log = LogManager.getLogger("base");

	@Autowired
	private MemberDao memberDao;

	@Autowired
	RestTemplate restTemplate; // REST API를 호출할 수 있는 내장 Srping 내장 클래스.
	
	@Autowired
	private PasswordEncoder passwordEncorder;
	
	@Value("${google.secret}")
	private String googleSecretClientId;
	
	@Value("${google.secretPW}")
	private String googleClientSecret;
	
	@Value("${google.redirectUrl}")
	private String googleRedirectUrl;
	
	@Value("${kakao.secret}")
	private String kakaoSecretClientId;

	@Value("${kakao.redirectUrl}")
	private String kakaoRedirectUrl;
	
	// 참고) https://2bmw3.tistory.com/15

//	// 카카오의 권한 토크를 받는 메소드
//	public String getKakaoAccessToken(String code) { 
//		log.debug("@Value 테스트: " + googleSecretClientId);
//		log.debug("@Value 테스트2: " + kakaoSecretClientId);
//		
//		String accessToken = "";
//
//		// restTemplate을 사용하여 API 호출
//		String reqUrl = "https://kauth.kakao.com/oauth/token"; // 해당 URL에 요청하여 Access token을 받는다.
//		URI uri = URI.create(reqUrl);
//
//		HttpHeaders headers = new HttpHeaders();
//
//		MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<String, Object>();
//		parameters.set("grant_type", "authorization_code");
//		parameters.set("client_id",kakaoSecretClientId); // 카카오 REST API 주소
//		parameters.set("redirect_uri",kakaoRedirectUrl); // 리다이렉트 주소
//		// 리다이렉트 주소 의미: 로그인에 성공하면 다시 로그인한 사용자가 만든 페이지로 돌아가야 하는데 그 돌아갈 페이지의 주소가
//		// redirect_uri
//		parameters.set("code", code); // 카카오에서 전달 받은 인가 코드
//		
//		HttpEntity<MultiValueMap<String, Object>> restRequest = new HttpEntity<>(parameters, headers); // 카카오에서 전달 받은
//																										// 파라미터 값을 인자로
//																										// 전달하여 전달.
//		ResponseEntity<JSONObject> apiResponse = restTemplate.postForEntity(uri, restRequest, JSONObject.class);
//		JSONObject responseBody = apiResponse.getBody();
//
//		accessToken = (String) responseBody.get("access_token");
//
//		return accessToken; // 카카오의 권한 토큰 발급.
//	}

	// 카카오 토큰을 통해 사용자 정보 가져오기. 
	public JsonNode getkakaoUserInfo(String accessToken) {
		String reqUrl = "https://kapi.kakao.com/v2/user/me"; // 요청을 받는 카카오 API 서버의 도메인에서  사용자 정보를 받아오기 위해서.
		URI uri = URI.create(reqUrl);

		HttpHeaders headers = new HttpHeaders(); // http 헤더에  아까 받은 토큰을 헤더에 넣고
		headers.set("Authorization", "bearer " + accessToken); 
		// Java의 Spring Framework에서 제공하는 인터페이스로, 하나의 키에 대해 여러 값을 저장할 수 있는 Map 타입
		MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<String, Object>();
		//는 "property_keys"이고, 값은 JSON 배열의 문자열 표현입니다.
		parameters.add("property_keys",
				"[\"kakao_account.profile\",\"kakao_account.name\",\"kakao_account.email\",\"kakao_account.birthday\"]");

		HttpEntity<MultiValueMap<String, Object>> restRequest = new HttpEntity<>(parameters, headers); // 위 헤더와 파라미터 값을 가지고 http requset 요청 수행
		return restTemplate.postForEntity(uri, restRequest, JsonNode.class).getBody();
		// 실제 http post 방식으로 요청 (위의 작성한 카카오 서버 uri, http request 정보, 응답 형태를 json으로 받으라는 의미)
	}

	// 카카오 로그인 및 회원가입
	public Member kakaoLogin(String token) {
		// 1. 먼저 카카오 서비스에 로그인 후 사용자 이용동의를 커치면 카카오에서 인가 코드를 전송해주는 방식으로 진행.
		// 2. 인가 코드를 통해 권한 토큰을 반환시킴.
		//String token = getKakaoAccessToken(code);
		// 3. 권한 토큰을 가지고 사용자 정보를 가지지는 형태로 나타냄.
		JsonNode userInfo = getkakaoUserInfo(token);
		log.debug(userInfo);
		// (1) 사용자 아이디 값을 가져와서 DB에 있는 지 여부를 확인 [카카오 로그인]
		Member memberCheck = socialEnrollconfirmation(userInfo);
		if (memberCheck != null) {
			return memberCheck;
		}
		
//		// (2) DB에 사용자 정보가 없으면 새롭게 가입시키면 됨.
		return KaKaoRegister(userInfo);
	}

	// DB에서 소셜사용자가 정보가 이미 등록되어 있는지 확인.
	public Member socialEnrollconfirmation(JsonNode userInfo) {

		String id = userInfo.get("id").asText().substring(0,10);
		Member member = memberDao.selectMemberById(id);
		return member;
	}

	// 카카오 로그인한 정보를 꾹꾹 서비스에 등록.
	public Member KaKaoRegister(JsonNode userInfo) {
		Member member = new Member();
		member.setMemberId(userInfo.get("id").asText().toString()); // 카카오에서 임의로 부여한 ID 값.
		member.setMemberSocial("KAKAO"); // 사용한 소셜 정보
		member.setMemberNickname(userInfo.get("kakao_account").get("profile").get("nickname").asText().toString()); // 카카오
																													// 프로필
																													// 이름
		member.setMemberEmail(userInfo.get("kakao_account").get("email").asText().toString()); // 카카오로 연동한 이메일

		// sql구문이 not null로 지정되어 있기에... member VO 나마지 값들을 더미 값으로 삽입.
		member.setMemberName(userInfo.get("kakao_account").get("profile").get("nickname").asText().toString());
		member.setMemberPw(passwordEncorder.encode("kakao"));
		member.setMemberPhone("");
		member.setMemberBirth(new Date(19700101)); // 카카오에 생일을 받아오나, 월 일 정도 만 불러와서. 임의로 우선 설정.
		member.setMemberAuthority("NORMAL"); // 우선 NORMAL 설정함. => 향후 GUEST
		member.setMemberAllowEmail(true);
		log.debug(member);
		try {
			memberDao.insertMember(member);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // 위 과정이 정상적으로 진행하면 회원가입이 정상적을 실행.

		return memberDao.selectMemberById(member.getMemberId());
	}

	// 1. 구글 로그인 하면 구글에서 리다이렉트한 코드를 받아오기
	// 2. 받아온 코드를 통해서 구글에서 제공하는 토큰을 받아오기.
	// 3. 반환 받은 토큰을 통해서 사용자 정보 가져오기. [전반적인 로직]
	// 참고) https://darrenlog.tistory.com/40

	// 구글의 리다이렉트한 전송환 코드를 통해 토큰을 가져오기
	public String getGoogleAccessToken(String code) {
		String tokenUri = "https://oauth2.googleapis.com/token"; // 토큰을 가지고 있는 주소

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("code", code);
		params.add("client_id",googleSecretClientId);
		params.add("client_secret",googleClientSecret);
		params.add("redirect_uri",googleRedirectUrl);
		params.add("grant_type", "authorization_code"); // 어플리케이션이 authorization_code 유형을 사용한다고 명시.

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		HttpEntity entity = new HttpEntity(params, headers);

		ResponseEntity<JsonNode> responseNode = restTemplate.exchange(tokenUri, HttpMethod.POST, entity,
				JsonNode.class);
		JsonNode accessTokenNode = responseNode.getBody();
		log.debug("getGoogle:"+accessTokenNode);
		log.debug("접근토큰 : "+ accessTokenNode.get("access_token").asText());
		return accessTokenNode.get("access_token").asText();

	}

	// 구글에서 전달 받은 토큰을 가지고 로그인한 구글 사용자 정보를 반환하기
//	public JsonNode getGoogleUserInfo(String token) {
//		String resouceUri = "https://www.googleapis.com/oauth2/v2/userinfo";
//
//		HttpHeaders headers = new HttpHeaders();
//		headers.set("Authorization", "Bearer " + token);
//		HttpEntity entity = new HttpEntity(headers);
//		return restTemplate.exchange(resouceUri, HttpMethod.GET, entity, JsonNode.class).getBody();
//	}
	
	public JsonNode getGoogleUserInfo(String token) {
	    String resourceUri = "https://www.googleapis.com/oauth2/v2/userinfo";
	    HttpHeaders headers = new HttpHeaders();
	    headers.setBearerAuth(token);
	    HttpEntity<?> entity = new HttpEntity<>(headers);
	    ResponseEntity<JsonNode> response = restTemplate.exchange(resourceUri, HttpMethod.GET, entity, JsonNode.class);
	    return response.getBody();
	}

	// 구글 로그인 및 회원 가입
	public Member googleLogin(String token) {
		//String token = getGoogleAccessToken(code);
		//log.debug(token);
		JsonNode userInfo = getGoogleUserInfo(token);
		Member memberCheck = socialEnrollconfirmation(userInfo);
		if (memberCheck != null) {
			return memberCheck;
		} else {
			
			return googleRegister(userInfo);
		}
	}

	// 구글 회원 가입
	private Member googleRegister(JsonNode userInfo) {
		Member member = new Member();
		member.setMemberId(userInfo.get("id").asText().substring(0, 10));
		member.setMemberEmail(userInfo.get("email").asText().toString());
		member.setMemberName(userInfo.get("name").asText().toString());
		member.setMemberSocial("GOOGLE");

		member.setMemberPw(passwordEncorder.encode("google"));
		member.setMemberBirth(new Date(19700101));
		member.setMemberNickname(userInfo.get("name").asText().toString());
		member.setMemberPhone("");
		member.setMemberAuthority("NORMAL");
		member.setMemberAllowEmail(true);
		log.debug(member);
		try {
			memberDao.insertMember(member);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // 위 과정이 정상적으로 진행하면 회원가입이 정상적을 실행.

		return member;
	}
	
}

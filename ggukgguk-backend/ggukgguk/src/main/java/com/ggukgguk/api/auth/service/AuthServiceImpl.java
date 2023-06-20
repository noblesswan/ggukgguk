package com.ggukgguk.api.auth.service;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.ggukgguk.api.auth.util.JwtTokenUtil;
import com.ggukgguk.api.auth.vo.AuthTokenPayload;
import com.ggukgguk.api.member.dao.MemberDao;
import com.ggukgguk.api.member.vo.Member;

@Service
public class AuthServiceImpl implements AuthService {
	
	private Logger log = LogManager.getLogger("base");

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Override
	public AuthTokenPayload login(Member member) {
		try {
			UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
					member.getMemberId(), member.getMemberPw());
			Authentication authentication = authenticationManager.authenticate(authenticationToken);

			AuthTokenPayload result = jwtTokenUtil.generateToken(authentication);
			return result; 
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public AuthTokenPayload regenToken(String refreshToken) {
		try {
			return jwtTokenUtil.reGenerateTokenFromRefreshToken(refreshToken);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}

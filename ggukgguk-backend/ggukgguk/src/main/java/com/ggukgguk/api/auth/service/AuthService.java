package com.ggukgguk.api.auth.service;

import java.util.HashMap;

import com.ggukgguk.api.auth.vo.AuthTokenPayload;
import com.ggukgguk.api.member.vo.Member;

public interface AuthService {

	public AuthTokenPayload login(Member member);
	public AuthTokenPayload regenToken(String refreshToken);
}

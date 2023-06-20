package com.ggukgguk.api.auth.security;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private String[] allowOrigins;
	
//	public JwtAuthenticationEntryPoint(String allowOriginsRaw) {
//		allowOrigins = new ArrayList<String>();
//		
//		StringTokenizer st = new StringTokenizer(allowOriginsRaw, ",");
//		while(st.hasMoreTokens()) {
//			allowOrigins.add(st.nextToken());
//		}
//	}
	
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
    	String requestOrigin = request.getHeader("Origin");
    	if (Arrays.asList(allowOrigins).contains(requestOrigin)) {
    		response.setHeader("Access-Control-Allow-Origin", requestOrigin);
    	}
    	
		response.setContentType("application/json; charset=UTF-8"); 
		
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		
		PrintWriter out = response.getWriter();
		out.print("{ \"status\": \"error\", \"message\": \"토큰이 만료되었거나 잘못되었습니다.\" }");
		out.close();
    }
}
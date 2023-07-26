package com.green.nowon.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
	//formlogin 로직 수행중 실패할경우 ajax의 응답으로 json 타입의 데이터를 return해줌
	// Map<String,boolean> 의 형태로 result: false를 리턴
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException exception) throws IOException, ServletException {
		Map<String, String> jsonResponse = new HashMap<>();
		jsonResponse.put("result", "false");
		jsonResponse.put("faliureURL","/emart/login?error=true");
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(new ObjectMapper().writeValueAsString(jsonResponse));
		response.getWriter().flush();
	}



}

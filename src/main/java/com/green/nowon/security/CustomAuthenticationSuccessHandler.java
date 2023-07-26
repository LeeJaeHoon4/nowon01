package com.green.nowon.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.green.nowon.product.domain.dto.TempDTO;
import com.green.nowon.product.service.ProductService;


@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
	
	@Autowired
	private ProductService service;

	private RequestCache requestCache = new HttpSessionRequestCache();
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	// formlogin 로직 수행중 성공할 경우 ajax의 응답으로 json 타입의 데이터를 return해줌

	// Map<String,boolean> 의 형태로 result: true를 리턴
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		

		
		//쿠키 확인.
		//쿠키가 있다면 세션에 들어있는 임시 장바구니를 진짜 장바구니로 옮기기
		Cookie[] CookieList = request.getCookies();
		
		if(CookieList != null) {			
		
		for(Cookie c : CookieList) {
			if(c.getName().equals("userId")) {
				HttpSession session = request.getSession();
				List<TempDTO> dtoList = (List<TempDTO>)session.getAttribute(c.getValue());
				
				//다 쓴 세션,쿠키 제거
				//세션제거
				session.removeAttribute(c.getValue());
				//쿠키제거
				Cookie myCookie = new Cookie("userId", null);  // 쿠키 값을 null로 설정
				myCookie.setMaxAge(0);  // 남은 만료시간을 0으로 설정
				response.addCookie(myCookie);
				
				//TempDTO 리스트와 계정 아이디 가지고 서비스로
				service.moveBasket(dtoList, authentication.getName());
				break;
			}
		}
		
		}

		/*
		 * String targetUrl = "/"; // 인증이 필요한 url요청시 인증되지 않은 사용자를 기본 로그인 페이지로 리디렉션함 //
		 * 인증이 완료되면 원래 이동하고자 하는 url로 이동해야하므로 그정보를 저장해 주는 객체 
		 * security를 사용한 longin에서 prefer페이지는 SavedRequest에서 확인 가능
		 * security를 사용한 로그인 >> 권한상 미달이라 로그인창으로 이동된경우
		 * SavedRequest savedRequest = this.requestCache.getRequest(request, response);
		 * System.out.println(savedRequest);
		 * 
			//내가 저장하고 싶은 prefer페이지를 컨트롤러에서 Session에서 저장해서 넘긴다
			//컨트롤러에서 singin 또는 signup일경우에 Session에 저장되어있는 prevPage를 덮어쓰지 않으면 
			// 이전에 보고있던 창으로 redirect가 가능하다
			HttpSession session =  request.getSession();
			String prevPage = (String)session.getAttribute("prevPage");
			System.out.println(prevPage);
		 * if (savedRequest != null) { 
		 * String redirectUrl = savedRequest.getRedirectUrl();
		 * 	if(!(String)savedRequest.getRedirectUrl().contains("/singin")){
		 * 	targetUrl = savedRequest.getRedirectUrl(); 
		 *  redirectStrategy.sendRedirect(request, response, targetUrl);
		 *  return;
		 * 	}
		 * }
		 * 
		 *  if(prevPage != null) {
				targetUrl = prevPage;
				session.removeAttribute("prevPage");
			}
		 */
	    
			 Map<String, String> jsonResponse = new HashMap<>();
			 jsonResponse.put("result","true");
			 response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			 response.setCharacterEncoding("UTF-8"); response.getWriter().write(new
			 ObjectMapper().writeValueAsString(jsonResponse));
			 response.getWriter().flush();
		//redirectStrategy.sendRedirect(request, response, targetUrl);

	}
}

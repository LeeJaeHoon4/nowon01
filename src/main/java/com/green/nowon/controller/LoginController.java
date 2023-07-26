package com.green.nowon.controller;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.Session;

import org.springframework.http.HttpRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;


@Controller
public class LoginController {

	@ApiOperation(value = "회원 가입 페이지 이동",httpMethod = "GET")
	@GetMapping("/emart/signup")
	public String signup() {
		return"/login/signup";
	}
	
	@ApiOperation(value = "로그인 페이지 이동",httpMethod = "GET")
	@GetMapping("/emart/login")
	public String login() {
		return"/login/login";
	}
	@ApiOperation(value = "로그인 팝업에서 회원가입 페이지 이동",httpMethod = "POST")
	@ApiImplicitParam(name = "data", value = "true던지면 성공 false던지면 실패", dataType = "boolean", paramType = "query")
	@PostMapping("/emart/signup")
	//@ResponseBody 어노테이션을 통해
	//메소드가 반환하는 값을 HTTP 응답 본문에 직접 쓰여지게 하여, 해당 값을 클라이언트가 받아볼 수 있도록 함
	@ResponseBody 
	public String signup(@RequestParam("data") boolean data) {
			String result = "/emart/signup";
			if(data) {
				return result;
			}else {
				return "error";
			}	
	}
	
	@ApiOperation(value = "권한 없을때 에러페이지로 이동하며 로그인창 띄우줍니다.",httpMethod = "GET")
	@GetMapping("/login/forlogin")
	public String moveToForlogin(HttpServletRequest request,Model model) {
		String target = request.getHeader("referer");
		model.addAttribute("target", target);
		return"/login/forlogin";
	}
}

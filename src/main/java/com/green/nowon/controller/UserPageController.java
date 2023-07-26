package com.green.nowon.controller;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import io.swagger.annotations.ApiOperation;

@Controller
public class UserPageController {
	
	@ApiOperation(value = "회원 페이지",httpMethod = "GET")
	@GetMapping("/user/mypage")
	public String toMypage(Authentication auth){
		return "/infopage/userpage";
	}
	
	@ApiOperation(value = "어드민 페이지",httpMethod = "GET")
	@GetMapping("/user/adminpage")
	public String toAdminpage(Authentication auth){
		return "/infopage/adminpage";
	}
	
	@ApiOperation(value = "마스터 페이지",httpMethod = "GET")
	@GetMapping("/user/masterpage")
	public String toMasterpage(Authentication auth){
		return "/infopage/masterpage";
	}
}

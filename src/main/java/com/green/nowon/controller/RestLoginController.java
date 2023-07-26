package com.green.nowon.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.green.nowon.service.LoginService;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value="/login/aouth2",produces = "application/json")
@RequiredArgsConstructor
public class RestLoginController {
	private final LoginService service;
	
	@ApiOperation(value = "소셜 로그인 provider별로 처리 사용X", httpMethod = "GET")
	@GetMapping("/code/{registrationId}")
	public void googleLogin(@RequestParam String code, @PathVariable String registrationId) {
		service.socialLogin(code,registrationId);
	}
}
